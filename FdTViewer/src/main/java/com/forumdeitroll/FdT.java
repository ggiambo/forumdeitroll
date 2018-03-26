package com.forumdeitroll;

import org.apache.jasper.servlet.TldScanner;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.SimpleInstanceManager;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;

public class FdT {

	public static void main(String[] args) throws Exception {
		// Create a basic jetty server object that will listen on port 8080.
		// Note that if you set this to port 0 then a randomly available port
		// will be assigned that you can either look in the logs for the port,
		// or programmatically obtain it for use in test cases.
		Server server = new Server(9090);

		// The WebAppContext is the entity that controls the environment in
		// which a web application lives and breathes. In this example the
		// context path is being set to "/" so it is suitable for serving
		// root context requests and then we see it setting the location of
		// the war. A whole host of other configurations are available,
		// ranging from configuring to support annotation scanning in the
		// webapp (through PlusConfiguration) to choosing where the webapp
		// will unpack itself.
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("target/FdTViewer-0.0.1-UPPETE.war");
		webapp.setParentLoaderPriority(true);


		webapp.setAttribute(JarScanner.class.getName(), new StandardJarScanner());

//		webapp.setBaseResource(Resource.newClassPathResource("classpath:/WEB-INF"));
		webapp.setBaseResource(Resource.newClassPathResource("/"));


		TldScanner scanner = new TldScanner(webapp.getServletContext(), true, false, false);
		scanner.scan();

		// A WebAppContext is a ContextHandler as well so it needs to be set to
		// the server so it is aware of where to send the appropriate requests.
		server.setHandler(webapp);

		webapp.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());


		// Start things up! By using the server.join() the server thread will
		// join with the current thread.
		// See http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.html#join()
		// for more details.
		server.start();
		server.dumpStdErr();
		server.join();
	}
}