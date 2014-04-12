** _This project is Work In Progress_ **
Al-muallim
================================
_Islamic Research Tool_
--------------------------------
Al-muallim is an Open Source, cross-platform application for reading,researching The Holy Quran and Authentic Hadiths. 
Al-muallim is a Netbeans Platform Application written in Java, JavaFX (JDK 8), HTML5, CSS3 and JavaScript.
The quranic text used in this application is taken from [noor-e-hidayat](http://noorehidayat.org/).

Building
--------
I am myself not good in build systems. So here is the complete NetBeans project, download it, open it and let the IDE handle it!

Features
--------
* **Simple Interface:**
Al-muallim has a very simple,easy to use interface, with emphasis on the content (Metro) rather than colours or images. (Another probable reason for this simplicity may be my inabililty to design, so made it simple and saying its Metro ;) )

* **The Holy Quran:**

* **Powerful Search:**
Al-muallim's search is powered by Lucene. 

* **Bookmarks:**
Al-muallim allows you to easily add/remove bookmarks. Also the bookmarks can be managed / organized using the bookmark manager.

* **Built-in Dictionary:**
Al-muallim is bundled with a highly configurable, open standard dictionary to lookup meanings while reading the translations. The dictionary format is based on XDXF and many more dictionaries can be added and are available free to download from ....


* **Wordpad:**
Al-muallim features a Rich Text Editor to take notes while you are doing your research.

Developer Notes
==============
Al-muallim is a modular, service based Netbeans Platform Application. This project uses the JavaFX WebView which had a few problems in rendering Arabic Texts (see ~~RT-13708~~,RT-LCD_TEXT_ISSUE) and hence the requirement on JDK 8.

* The NetBeans Plaform provides a central service management with the lookup concept which enables 
us to use certain services provided within an application independently of each other. In this application all the services are included in ```Services Module```.
* The database has been implemented using H2 and can be initialized and used as
```java
   Database database = Lookup.getDefault().lookup(Database.class);
```
If another implementation of database exists, that can queried by using the lookupAll method
```java
   Collection<? extends Database> databases = Lookup.getDefault().lookupAll(Database.class);
   foreach(Database db : databases)
   {
       if(db instanceof MyDatabaseClass)
       {
          //do your thing
       }
   }
```

* The ``` BrowserAddin``` service is used to add a fucntionality to existing modules.see the next section for more details.

* SearchProvider
The search provided by using Lucene.

* **Creating an Addin for existing modules:**
To create an addin for existing modules, just implement the ```BrowserAddin``` service. There are a bunch of utility JScript functions available to use for every Browser Addin.Check Browser\release\www\js\global.js
