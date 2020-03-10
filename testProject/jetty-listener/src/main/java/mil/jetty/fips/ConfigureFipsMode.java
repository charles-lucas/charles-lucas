package mil.jetty.fips;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.lang.SecurityManager;

import org.bouncycastle.crypto.CryptoServicesPermission;
import org.bouncycastle.crypto.fips.FipsStatus;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;


/**
 * Class used for configuring FIPS mode for security related operations.
 * <p>
 * Currently using the Bouncy Castle library for security related functions. See <link>BouncyCastleFipsProvider</link>
 * in the Bouncy Castle library.
 * 
 * @see BouncyCastleFipsProvider
 */
public class ConfigureFipsMode {

  private static Boolean fipsModeConfigured = false;
  private static final Permission AbleToUseUnapprovedMode = new CryptoServicesPermission("unapprovedModeEnabled");

  /**
   * Configure the system to use FIPS mode.
   * 
   * @return <b>true</b> if configured for FIPS mode. <b>false</b> otherwise.
   */
  @SuppressWarnings("restriction")
  public synchronized static boolean configureFipsMode() {
    BouncyCastleFipsProvider bcFipsProvider = new BouncyCastleFipsProvider();
    
    Provider[] providers = Security.getProviders();
    
    System.out.println("Providers - Before Initialization: " );
    
    for (Provider provider: providers) {
      System.out.println("   Provider: " + provider);      
    }
    
    
    // check if provider was already registered, only need to do it once
    if (Security.getProvider(bcFipsProvider.getName()) == null) {
      System.out.println("1 - configureFipsMode()");
      System.out.println("2 - configureFipsMode()");
      // System.setProperty("java.security.policy",
      // "file:${java.home}/lib/security/jenm.policy"); //need to
      // install this file and we need to point to it
      System.out.println("3 - configureFipsMode()");
      // System.setSecurityManager(new SecurityManager());
      System.out.println("4 - configureFipsMode()");
      System.out.println("Configuring FIPS with provider..." + bcFipsProvider.getName());
      
      /*
      Can not remove SunJSSE as it was done previously. In the Java 8 version, there was support
      to enable a "experimental" fips mode which extended off SunJSSE. This experimental mode has been removed in Java 11.
      Removing the provider SunJSSE will break the system as it still depends on the SunJSSE provider for the SunX509 KeyManagerFactory.
       */
      // Security.removeProvider("SunJSSE");

      // remove the providers if they exist
      Security.removeProvider("BCFIPS");
      Security.removeProvider("BCJSSE");

      // first insert BCFIPS, used by BCJSSE for initialization
      Security.insertProviderAt(bcFipsProvider, 1);
  	  
      // insertion of BCJSSE, this is used by key/trust store managers
      BouncyCastleJsseProvider bcJsseProvider = new BouncyCastleJsseProvider("fips:BCFIPS");
      Security.insertProviderAt(bcJsseProvider, 2);

      
	  System.out.println("5 - configureFipsMode()");
      
	  fipsModeConfigured = (FipsStatus.isReady() && isDefaultModeFips());
	  
      System.out.println("In FIPS (only) mode: " + fipsModeConfigured);
      System.out.println("Configuring FIPS with provider..." + bcFipsProvider.getName());
      System.out.println("Configuring FIPS with provider..." + bcJsseProvider.getName());

      
      System.out.println("Providers - After Initialization: " );
      
      providers = Security.getProviders();
      for (Provider provider: providers) {
        System.out.println("   Provider: " + provider);      
      }

    }
    System.out.println("FIPS configure status: " + fipsModeConfigured);
    return fipsModeConfigured;
  }

  private static boolean isDefaultModeFips() {
    FipsStatus.isReady();
    System.out.println("1 - isDefaultModeFips()");
    boolean result = true;
    try {
      System.out.println("2 - isDefaultModeFips()");
      System.out.println("Checking FIPS Unapproved mode...");
      checkPermission(AbleToUseUnapprovedMode);
      System.out.println("3 - isDefaultModeFips()");
      result = false;
    } catch (SecurityException e) {
      System.out.println("4 - isDefaultModeFips()" + e);
      /* do nothing */
      System.out.println("FIPS Unapproved Mode not permitted!");
    }
    return result;
  }


  private static void checkPermission(final Permission permission) throws SecurityException {
    System.out.println("1 - checkPermission()");
    final SecurityManager securityManager = System.getSecurityManager();
    System.out.println("2 - checkPermission()");
    if (securityManager != null) {
      System.out.println("3 - checkPermission()");
      AccessController.doPrivileged(new PrivilegedAction<Object>() {
        public Object run() {
          System.out.println("4 - checkPermission()");
          securityManager.checkPermission(permission);
          System.out.println("5 - checkPermission()");
          return null;
        }
      });
    }
  }
}
