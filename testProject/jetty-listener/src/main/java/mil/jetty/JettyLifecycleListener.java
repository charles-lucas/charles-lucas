

package mil.jetty;

import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;

import mil.jetty.fips.ConfigureFipsMode;

import java.io.File;

/**
 * Listener object to handle Jetty startup/stop events.
 * 
 */
public class JettyLifecycleListener extends AbstractLifeCycleListener {

//  private JettyLifecycleFileControl jettyFileCtrl;
  /**
   * JETTY life cycle listener Constructor
   */
  public JettyLifecycleListener() {
    ConfigureFipsMode.configureFipsMode();
  }

  /**
   * Override the default JETTY life cycle listener on lifeCycleStarting process
   */
  @Override
  public void lifeCycleStarting(LifeCycle event) {
    System.out.println("Jetty Starting");
    /**
     * In order to ensure JENM Services come up cleanly and have not been corrupted by external processes, we
     * will perform cleanup of any temporary files leftover from previous deployment of JENM Services.  This
     * include system shutdown, reboot, and or Jenm Launcher start/stop.
     */
//    this.jettyFileCtrl.cleanJettyTmpDeployDirectories();
  }

  /**
   * Override the default JETTY life cycle listener on lifeCycleStopping process
   */
  @Override
  public void lifeCycleStopping(LifeCycle event) {
    System.out.println("Jetty Stopping");
    System.clearProperty("hostPort");
  }

}
