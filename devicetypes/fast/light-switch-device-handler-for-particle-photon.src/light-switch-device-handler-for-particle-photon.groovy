preferences {
    input(
    	name: "accessToken", 
        type: "text", 
    	title: "Access Token", 
    	required: true, 
    	displayDuringSetup: true
    )
    input(
    	name: "deviceId",  
        type: "text", 
        title: "Device ID", 
        required: true, 
        displayDuringSetup: true
    )
    input(
    	name: "particleFunctionNameToTurnLightsOn",  
        type: "text", 
        title: "Particle Function Name to Turn Lights On", 
        required: true, 
        displayDuringSetup: true
    )
    input(
    	name: "particleFunctionNameToTurnLightsOff",  
    	type: "text", 
        title: "Particle Function Name to Turn Lights Off", 
        required: true, 
        displayDuringSetup: true
    )
    input(
    	name: "particleVariableNameToCheckIfLightsAreOn",  
    	type: "text", 
        title: "Particle Variable Name to Check If Lights Are On", 
        required: true, 
        displayDuringSetup: true
    )
}

metadata {
	definition (
        namespace: "Fast", 
        name: "Light Switch Device Handler For Particle Photon", 
        author: "Nathan Fast",
        cstHandler: true
    ) {
    	//https://docs.smartthings.com/en/latest/capabilities-reference.html?highlight=capabilities
        capability "Switch"
        capability "Refresh"
	}
    tiles(scale: 2) {
        standardTile("switch", "device.switch", width: 6, height: 6, canChangeIcon: false) {
            state "off", label: '${name}', action: "switch.on", icon: "st.Lighting.light13", backgroundColor: "#ffffff"
            state "on", label: '${name}', action: "switch.off", icon: "st.Lighting.light13", backgroundColor: "#79b821", iconColor: "#ffffff"
        }

        main(["switch"])   
        
        details(["switch"])
    }
}

// miscellaneous

def parse(String description) {
	log.debug "Parsing '${description}'"
	return null
}

def installed() {
    particle_checkIfLightsAreOn()
}

// "Refresh" Capability Commands

command "refresh"
def refresh() {
	particle_checkIfLightsAreOn()
}

// custom commands

command "on"
def on(){
	particle_turnLightsOn()
}

command "off"
def off() {
	particle_turnLightsOff()
}

// particle api

def particle_turnLightsOn() {
    post("${particleFunctionNameToTurnLightsOn}", '')
}

def particle_turnLightsOff() {
    post("${particleFunctionNameToTurnLightsOff}",'')
}

def particle_checkIfLightsAreOn() {
    return get("${particleVariableNameToCheckIfLightsAreOn}")
}

def post(method, arg) {
    log.debug "---post(${method}, ${arg})---"
	httpPost([
	    'uri' : "https://api.particle.io/v1/devices/${deviceId}/${method}",
        'contentType': "application/x-www-form-urlencoded",
        'body' : [
            'access_token' : "${accessToken}",
            'arg' : arg 
        ]
    ]) {
    	response ->
        	log.debug "Response Received"
            log.debug "respone.status: ${response.status}"
    }
}

def get(variable) {
	try {
        log.debug "---get(${variable})---"
    	httpGet([
            'uri' : "https://api.particle.io/v1/devices/${deviceId}/${variable}?access_token=${accessToken}"
        ]) {
    		response ->
                log.debug "Response Received"
                log.debug "respone.status: ${response.status}"
                log.debug "response.data.name: ${response.data.name}"
                log.debug "response.data.result: ${response.data.result}"
                
                if(response.data.name == "${particleVariableNameToCheckIfLightsAreOn}" && response.data.result){
                    log.debug "on"
                	sendEvent(name: "switch", value: "on", displayed:true)
                }
                else{
                	log.debug "off"
                	sendEvent(name: "switch", value: "off", displayed:true)
                }
        }
   	} catch (e) {
        log.debug "something went wrong: $e"
	}
}