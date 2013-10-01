/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.almuallim.service.url;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Custom URL implementation for Al'muallim.
 * <code>almuallim://[modulename]?className=org.class.path&key=value</code>
 * There can be any number of key=value pairs. the class name specified should
 * implement
 * <code>AlmuallimURLOpener</code> interface
 *
 * @see AlmuallimURLOpener
 * @author Naveed Quadri
 */
public class AlmuallimURL {

    
    public static String ClassNameKey = "className";

    /**
     * convenience method to get a parameter from the url string  
     * @param url 
     * @param key
     * @return 
     */
    public static String getParameter(String url, String key) {
        String value = "";
        try {
            AlmuallimURL almuallimURL = new AlmuallimURL(url);
            value = almuallimURL.getParameters().get(key);
        } catch (MalformedURLException mfe) {
            //ignore
        }
        return value;

    }
    private String url;
    private static final String PROTOCOL = "almuallim";
    private String moduleName;
    private Map<String, String> parameters;

    /**
     * Constructs a AlmuallimURL Object from the given url string
     * @param url
     * @throws MalformedURLException 
     */
    public AlmuallimURL(String url) throws MalformedURLException {
        this.url = url;
        //almuallim://modulename?className=org.class.path&key=value
        if (!url.startsWith(PROTOCOL)) {
            throw new MalformedURLException("protocol not supported");
        }
        //remove the protocol
        //+3 for://
        url = url.substring(PROTOCOL.length() + 3);
        moduleName = url.substring(0, url.indexOf('?'));
        parameters = new HashMap<>();
        //remove the  '?'
        //+1 for '?'
        url = url.substring(moduleName.length() + 1);

        String[] params = url.split("&");
        if (params != null) {
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue == null || keyValue.length != 2) {
                    continue;
                }
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
    }

    

    /**
     * Copy Constructor
     * @param source 
     */
    public AlmuallimURL(AlmuallimURL source)
    {
        moduleName = source.moduleName;
        url = source.url;
        parameters = new HashMap<>(); 
        for (Map.Entry<String, String> entry : source.parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            parameters.put(key, value);
        }
    }
    
    
    

    public String getUrl() {
        return toString();
    }

    public String getModuleName() {
        return moduleName;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.url);
        hash = 41 * hash + Objects.hashCode(this.moduleName);
        hash = 41 * hash + Objects.hashCode(this.parameters);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AlmuallimURL other = (AlmuallimURL) obj;
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        if (!Objects.equals(this.moduleName, other.moduleName)) {
            return false;
        }
        if (!Objects.equals(this.parameters, other.parameters)) {
            return false;
        }
        return true;
    }

    @Override
    /*
     * had to re generate the string every time as url parameters can change
     * within the lifetime of this object
     */
    public String toString() {
        StringBuilder sb = new StringBuilder(PROTOCOL + "://" + moduleName + "?");
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("&");
        }
        //remove the last &
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
//        return url;
    }
}
