package magicUWE.actions.context.requirements;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectEventListener;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.awt.Point;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author PST LMU
 */
public class TransformationsListener implements ProjectEventListener, MouseListener, PropertyChangeListener
{
    private Point cOld;
    private boolean bStarted;
    private Timer cTimer;

    private static TransformationsListener cListener=null;

    /**
     *
     */
    private static class SceduledListenerAddition extends TimerTask
    {
        private DiagramPresentationElement cElement;
        private TransformationsListener cMaster;
        public SceduledListenerAddition(DiagramPresentationElement cElement,TransformationsListener cMaster)
        {
            this.cElement=cElement;
            this.cMaster=cMaster;
        }

        public void run()
        {
            if (cElement.getPanel()!=null)
            {
                cElement.getPanel().getDrawArea().getCanvas().addMouseListener(cMaster);
            }
        }
    }

    /**
     *
     * @return
     */
    public static TransformationsListener getListener()
    {
        if (cListener==null)
        {
            cListener=new TransformationsListener();
        }

        return(cListener);
    }

    /**
     *
     */
    private TransformationsListener()
    {
        bStarted=false;
        cOld=new Point(0,0);
        cTimer=new Timer();

        resetListeners();
    }

    /**
     *
     */
    public void resetListeners()
    {
        if ((Application.getInstance()==null)||(Application.getInstance().getProject()==null))
        {
            return;
        }

        Iterator<DiagramPresentationElement> cIterator=Application.getInstance().getProject().getDiagrams().iterator();
        while (cIterator.hasNext())
        {
            DiagramPresentationElement cTemp=cIterator.next();
            cTemp.addPropertyChangeListener(this);
                    
            if (cTemp.getPanel()!=null)
            {
                cTemp.getPanel().getDrawArea().getCanvas().addMouseListener(this);
            }
        }
    }

    /**
     *
     * @param event
     */
    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
        if (event.getPropertyName().equals("DiagramLoaded")&&event.getNewValue().equals(true))
        {
            if (event.getSource() instanceof DiagramPresentationElement)
            {
                try
                {
                    cTimer = new Timer();
                    cTimer.schedule(new SceduledListenerAddition(((DiagramPresentationElement)(event.getSource())),this),1000);
                }
                catch (Exception e)
                {
                    Application.getInstance().getGUILog().showError(e.toString());
                }
            }
        }
    }

    /**
     *
     * @param bSet
     */
    public void setStarted(boolean bSet)
    {
        bStarted=bSet;
    }

    /**
     *
     * @return
     */
    public Point getPosition()
    {
        return(cOld);
    }

    /**
     *
     * @param event
     */
    @Override
    public void mouseExited(MouseEvent event)
    {

    }

    /**
     *
     * @param event
     */
    @Override
    public void mouseEntered(MouseEvent event)
    {

    }

    /**
     *
     * @param event
     */
    @Override
    public void mouseReleased(MouseEvent event)
    {

    }

    /**
     *
     * @param event
     */
    @Override
    public void mousePressed(MouseEvent event)
    {

    }

    /**
     *
     * @param event
     */
    @Override
    public void mouseClicked(MouseEvent event)
    {
        if ((!bStarted)&&(Application.getInstance().getProject().getActiveDiagram().getPanel().getMousePosition()!=null))
        {
            cOld=Application.getInstance().getProject().getActiveDiagram().getPanel().getMousePosition();
            cOld.x+=Application.getInstance().getProject().getActiveDiagram().getVisibleBounds().x;
            cOld.y+=Application.getInstance().getProject().getActiveDiagram().getVisibleBounds().y;
        }
    }

    /**
     *
     * @param prjct
     */
    @Override
    public void projectActivated(Project prjct)
    {

    }

    /**
     *
     * @param prjct
     */
    @Override
    public void projectClosed(Project prjct)
    {

    }

    /**
     *
     * @param prjct
     */
    @Override
    public void projectDeActivated(Project prjct)
    {

    }

    /**
     *
     * @param prjct
     */
    @Override
    public void projectOpened(Project prjct) 
    {
        resetListeners();
    }

    /**
     *
     * @param prjct
     * @param prjct1
     */
    @Override
    public void projectReplaced(Project prjct, Project prjct1)
    {

    }

    /**
     *
     * @param prjct
     * @param bln
     */
    @Override
    public void projectSaved(Project prjct, boolean bln)
    {

    }

	@Override
	public void projectActivatedFromGUI(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectCreated(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectOpenedFromGUI(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectPreActivated(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectPreClosed(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectPreClosedFinal(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectPreDeActivated(Project arg0) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectPreReplaced(Project arg0, Project arg1) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

	@Override
	public void projectPreSaved(Project arg0, boolean arg1) {
		// Auto-generated method stub for MagicDraw 18.0 OpenApi
		
	}

}
