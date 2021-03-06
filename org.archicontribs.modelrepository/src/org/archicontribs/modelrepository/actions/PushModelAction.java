/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package org.archicontribs.modelrepository.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.archicontribs.modelrepository.IModelRepositoryImages;
import org.archicontribs.modelrepository.authentication.UsernamePassword;
import org.archicontribs.modelrepository.grafico.IRepositoryListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.ui.IWorkbenchWindow;

import com.archimatetool.model.IArchimateModel;

/**
 * Push Model Action ("Publish")
 * 
 * 1. Offer to save the model
 * 2. If there are changes offer to Commit
 * 3. Get credentials for Push
 * 4. Check Proxy
 * 5. Pull from Remote
 * 6. Handle Merge conflicts
 * 7. Push to Remote
 * 
 * @author Phillip Beauvoir
 */
public class PushModelAction extends RefreshModelAction {
    
    public PushModelAction(IWorkbenchWindow window) {
        super(window);
        setImageDescriptor(IModelRepositoryImages.ImageFactory.getImageDescriptor(IModelRepositoryImages.ICON_PUSH));
        setText(Messages.PushModelAction_0);
        setToolTipText(Messages.PushModelAction_1);
    }

    public PushModelAction(IWorkbenchWindow window, IArchimateModel model) {
        super(window, model);
    }

    @Override
    protected IRunnableWithProgress getHandler(UsernamePassword up) {
        return new PushHandler(up);
    }
    
    protected class PushHandler extends RefreshHandler {
        PushHandler(UsernamePassword up) {
            super(up);
        }
        
        @Override
        public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
            this.monitor = monitor;
            
            try {
                if(doPull()) {
                    monitor.beginTask(Messages.PushModelAction_3, IProgressMonitor.UNKNOWN);
                    getRepository().pushToRemote(up.getUsername(), up.getPassword(), this);
                    notifyChangeListeners(IRepositoryListener.HISTORY_CHANGED);
                }
            }
            catch(GitAPIException | IOException ex) {
                displayErrorDialog(Messages.PushModelAction_0, ex);
            }
            finally {
                monitor.done();
            }
        }
    }
}
