# SmartThingsPublic
A library of my own SmartThings Device Handlers, mostly wrapping functionality from Particle Photon devices.






For more information on how this Repository is structured visit:

https://docs.smartthings.com/en/latest/tools-and-ide/github-integration.html

> Each SmartApp and Device Handler should be in its own directory, named the same as the SmartApp or Device Handler, and appended with ".src".
> 
> For SmartApps:
> 
> smartapps/<namespace>/<smartapp-name>.src/<smartapp file>.groovy
> 
> For Device Handlers:
> 
> devicetypes/<namespace>/<device-type-name>.src/<device handler file>.groovy
> 
> The namespace is typically your GitHub user name. When you create a SmartApp or Device Handler in the IDE, you provide a namespace, which is then populated in the definition method. This namespace will be used in the directory structure as shown above.

> Note that the directory names must all be lowercase and must be consistent with the namespace and the name of the Device Handler or SmartApp. In other words, the directory names must all be lowercase with non-alphanumeric characters replaced with a dash. For example, if a SmartApp has the namespace “My Apps” and the name “My First App” then the path name for it must be smartapps/my-apps/my-first-app.src/my-first-app.groovy.
