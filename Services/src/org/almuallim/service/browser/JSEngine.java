package org.almuallim.service.browser;

/**
 * An interface to enable the execution of Javascript from or calling a java
 * function from Javascript
 *
 * @author Naveed Quadri
 */
public interface JSEngine {

    /**
     * Executes a script in the context of the current page.
     *
     * @param script
     * @return execution result, converted to a Java object using the following
     * rules
     * <ul>
     * <li>JavaScript Int32 is converted to java.lang.Integer</li>
     * <li>Other JavaScript numbers to java.lang.Double</li>
     * <li>JavaScript string to java.lang.String</li>
     * <li>JavaScript boolean to java.lang.Boolean</li>
     * <li>JavaScript null to null</li>
     * <li>Most JavaScript objects get wrapped as
     * netscape.javascript.JSObject</li>
     * <li>JavaScript JSNode objects get mapped to instances of
     * netscape.javascript.JSObject, that also implement org.w3c.dom.Node</li>
     * <li>A special case is the JavaScript class JavaRuntimeObject which is
     * used to wrap a Java object as a JavaScript value - in this case we just
     * extract the original Java value.</li>
     * </ul>
     */
    public Object executeScript(String script);

    /**
     * The registerJavaFunction method is useful to enable upcalls from
     * JavaScript into Java code, as illustrated by the following example. The
     * Java code establishes a new JavaScript object named app. This object has
     * one public member, the method exit.
     *
     *
     * <code><pre>
     * public class JavaApplication
     * {
     * public void exit()
     * {
     * Platform.exit();
     * }
     * }
     *
     * registerJavaFunction("app", new JavaApplication());
     * </pre></code>
     * You can then refer to the object and the method from your
     * HTML page:
     *
     * <code><pre>&lt;a href="" onclick="app.exit()"&gt;Click here to exit application&lt;/a&gt; </pre></code>
     *
     * When a user clicks the link the application is closed.
     *
     * @param name
     * @param member
     */
    public void registerJavaFunction(String name, Object member);
}
