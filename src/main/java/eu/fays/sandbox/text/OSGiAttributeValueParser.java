package eu.fays.sandbox.text;

import static java.lang.System.out;

/**
 * Parse the value of an OSGi attribute from a Manifest file<br>
 * E.g. attributes: Import-Packages, Export-Packages.
 */
@SuppressWarnings("nls")
public class OSGiAttributeValueParser {

	/**
	 * @param args unused
	 */
	public static void main(final String[] args) {
		final String value = 
"""
 org.eclipse.core.runtime.adaptor;x-friends:="org.eclipse
 .core.runtime",org.eclipse.core.runtime.internal.adaptor;x-internal:=tr
 ue,org.eclipse.equinox.log;version="1.1";uses:="org.osgi.framework,org.
 osgi.service.log",org.eclipse.osgi.container;version="1.6"; uses:="org.
 eclipse.osgi.report.resolution,  org.osgi.framework.wiring,  org.eclips
 e.osgi.framework.eventmgr,  org.osgi.framework.startlevel,  org.osgi.fr
 amework,  org.osgi.framework.hooks.resolver,  org.osgi.service.resolver
 ,  org.osgi.resource,  org.eclipse.osgi.service.debug",org.eclipse.osgi
 .container.builders;version="1.0";uses:="org.eclipse.osgi.util,org.ecli
 pse.osgi.container",org.eclipse.osgi.container.namespaces;version="1.0"
 ;uses:="org.osgi.resource",org.eclipse.osgi.framework.console;version="
 1.1";uses:="org.osgi.framework",org.eclipse.osgi.framework.eventmgr;ver
 sion="1.2",org.eclipse.osgi.framework.internal.reliablefile;x-internal:
 =true,org.eclipse.osgi.framework.log;version="1.1";uses:="org.osgi.fram
 ework",org.eclipse.osgi.framework.util;x-internal:=true,org.eclipse.osg
 i.internal.debug;x-internal:=true,org.eclipse.osgi.internal.framework;x
 -internal:=true,org.eclipse.osgi.internal.hookregistry;x-friends:="org.
 eclipse.osgi.tests",org.eclipse.osgi.internal.loader;x-internal:=true,o
 rg.eclipse.osgi.internal.loader.buddy;x-internal:=true,org.eclipse.osgi
 .internal.loader.classpath;x-internal:=true,org.eclipse.osgi.internal.l
 oader.sources;x-internal:=true,org.eclipse.osgi.internal.location;x-int
 ernal:=true,org.eclipse.osgi.internal.messages;x-internal:=true,org.ecl
 ipse.osgi.internal.provisional.service.security;version="1.0.0";x-frien
 ds:="org.eclipse.equinox.security.ui",org.eclipse.osgi.internal.provisi
 onal.verifier;x-friends:="org.eclipse.ui.workbench,org.eclipse.equinox.
 p2.artifact.repository",org.eclipse.osgi.internal.service.security;x-fr
 iends:="org.eclipse.equinox.security.ui",org.eclipse.osgi.internal.serv
 iceregistry;x-internal:=true,org.eclipse.osgi.internal.signedcontent;x-
 internal:=true,org.eclipse.osgi.internal.url;x-internal:=true,org.eclip
 se.osgi.launch;version="1.1";uses:="org.osgi.framework,org.osgi.framewo
 rk.launch,org.osgi.framework.connect",org.eclipse.osgi.report.resolutio
 n;version="1.0";uses:="org.osgi.service.resolver,org.osgi.resource",org
 .eclipse.osgi.service.datalocation;version="1.4.0",org.eclipse.osgi.ser
 vice.debug;version="1.2",org.eclipse.osgi.service.environment;version="
 1.4",org.eclipse.osgi.service.localization;version="1.1";uses:="org.osg
 i.framework",org.eclipse.osgi.service.pluginconversion;version="1.0",or
 g.eclipse.osgi.service.resolver;version="1.6";uses:="org.osgi.framework
 ,org.osgi.framework.hooks.resolver,org.osgi.framework.wiring",org.eclip
 se.osgi.service.runnable;version="1.1",org.eclipse.osgi.service.securit
 y;version="1.0",org.eclipse.osgi.service.urlconversion;version="1.0",or
 g.eclipse.osgi.signedcontent;version="1.1";uses:="org.osgi.framework",o
 rg.eclipse.osgi.storage;x-friends:="org.eclipse.osgi.tests",org.eclipse
 .osgi.storage.bundlefile;x-internal:=true,org.eclipse.osgi.storage.url.
 reference;x-internal:=true,org.eclipse.osgi.storagemanager;version="1.0
 ",org.eclipse.osgi.util;version="1.1",org.osgi.dto;version="1.1.1",org.
 osgi.framework;version="1.10",org.osgi.framework.connect;version="1.0";
 uses:="org.osgi.framework.launch",org.osgi.framework.dto;version="1.8";
 uses:="org.osgi.dto",org.osgi.framework.hooks.bundle;version="1.1";uses
 :="org.osgi.framework",org.osgi.framework.hooks.resolver;version="1.0";
 uses:="org.osgi.framework.wiring",org.osgi.framework.hooks.service;vers
 ion="1.1";uses:="org.osgi.framework",org.osgi.framework.hooks.weaving;v
 ersion="1.1";uses:="org.osgi.framework.wiring",org.osgi.framework.launc
 h;version="1.2";uses:="org.osgi.framework",org.osgi.framework.namespace
 ;version="1.2";uses:="org.osgi.resource",org.osgi.framework.startlevel;
 version="1.0";uses:="org.osgi.framework",org.osgi.framework.startlevel.
 dto;version="1.0";uses:="org.osgi.dto",org.osgi.framework.wiring;versio
 n="1.2";uses:="org.osgi.framework,org.osgi.resource",org.osgi.framework
 .wiring.dto;version="1.3";uses:="org.osgi.dto,org.osgi.resource.dto",or
 g.osgi.resource;version="1.0.1",org.osgi.resource.dto;version="1.0.1";u
 ses:="org.osgi.dto",org.osgi.service.condition;version="1.0",org.osgi.s
 ervice.condpermadmin;version="1.1.2";uses:="org.osgi.framework",org.osg
 i.service.log;version="1.5";uses:="org.osgi.framework",org.osgi.service
 .log.admin;version="1.0";uses:="org.osgi.service.log",org.osgi.service.
 packageadmin;version="1.2.1";uses:="org.osgi.framework",org.osgi.servic
 e.permissionadmin;version="1.2.1",org.osgi.service.resolver;version="1.
 1.1";uses:="org.osgi.resource",org.osgi.service.startlevel;version="1.1
 .1";uses:="org.osgi.framework",org.osgi.service.url;version="1.0.1",org
 .osgi.util.tracker;version="1.5.3";uses:="org.osgi.framework"
""";				 

		/** opening double quote flag */
		boolean q = false;

		for(final char c : value.toCharArray()) {
			if (c == '"') {
				q = !q;
			}

			if (c != ' ' && c != '\r' && c != '\n') {
				out.print(c);
			}

			if (c == ',' && !q) {
				out.println();
				out.print(' ');
			}
		}

		out.println();
	}
	
}
