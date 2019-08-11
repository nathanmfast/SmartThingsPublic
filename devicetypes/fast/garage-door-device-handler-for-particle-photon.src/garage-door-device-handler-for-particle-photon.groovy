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
    	name: "particleFunctionNameToToggleDoor",  
        type: "text", 
        title: "Particle Function Name to Toggle Door", 
        required: true, 
        displayDuringSetup: true
    )
    input(
    	name: "particleVariableNameToCheckIfDoorIsClosed",  
    	type: "text", 
        title: "Particle Variable Name to Check If Door Is Closed", 
        required: true, 
        displayDuringSetup: true
    )
}

metadata {
    definition (
    	namespace: "Fast", 
        name: "Garage Door Device Handler For Particle Photon", 
        author: "Nathan Fast", 
        cstHandler: true
    ) {
        //https://docs.smartthings.com/en/latest/capabilities-reference.html?highlight=capabilities
		capability "Door Control"
        capability "Refresh"
        attribute "door", "String"
	}

	tiles {
		standardTile("toggle", "device.door", width: 2, height: 2) {
			state("unknown", label:'${name}', action:"refresh.refresh", icon:"st.doors.garage.garage-open", backgroundColor:"#ffffff")
			state("closed", label:'${name}', action:"door control.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#00a0dc", nextState:"opening")
			state("open", label:'${name}', action:"door control.close", icon:"st.doors.garage.garage-open", backgroundColor:"#e86d13", nextState:"closing")
			state("opening", label:'${name}', icon:"st.doors.garage.garage-opening", backgroundColor:"#e86d13")
			state("closing", label:'${name}', icon:"st.doors.garage.garage-closing", backgroundColor:"#00a0dc")
		}
        
		standardTile("refresh", "device.door", inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		main "toggle"
        
		details(["toggle", "refresh"])
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Executing 'parse('${description}')''"
	return null
}

def installed() {
    log.debug "Executing 'installed'"
    particle_checkIfDoorIsClosed()
}

// handle commands

def open() {
	log.debug "Executing 'open'"
    particle_toggleDoor()
    runIn(10, refresh)
}

def close() {
	log.debug "Executing 'close'"
	particle_toggleDoor()
    runIn(10, refresh)
}

def refresh() {
	log.debug "Executing 'refresh'"
    poll()
}

def poll() {
	log.debug "Executing 'poll'"
    particle_checkIfDoorIsClosed()
}

// particle api convenience methods

def particle_toggleDoor() {
    post("${particleFunctionNameToToggleDoor}", "")
}

def particle_checkIfDoorIsClosed() {
    return get("${particleVariableNameToCheckIfDoorIsClosed}")
}

// particle api post/get

def post(method, arg) {
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
    	httpGet([
            'uri' : "https://api.particle.io/v1/devices/${deviceId}/${variable}?access_token=${accessToken}"
        ]) {
    		response ->
                log.debug "Response Received"
                log.debug "respone.status: ${response.status}"
                log.debug "response.data.name: ${response.data.name}"
                log.debug "response.data.result: ${response.data.result}"
                
                if(response.data.name == "${particleVariableNameToCheckIfDoorIsClosed}" && !!response.data.result){
                    log.debug "closed"
                	sendEvent(name: "door", value: "closed", displayed:true)
                }
                else{
                	log.debug "open"
                	sendEvent(name: "door", value: "open", displayed:true)
                }
    			
            }
   	} catch (e) {
    	log.error "something went wrong: $e"
	}
}