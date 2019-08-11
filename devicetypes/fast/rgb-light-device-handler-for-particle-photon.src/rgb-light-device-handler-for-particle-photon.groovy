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
}

metadata {
    definition (
    	namespace: "Fast", 
        name: "RGB Light Device Handler For Particle Photon", 
        author: "Nathan Fast", 
        cstHandler: true
    ) {
    	//https://docs.smartthings.com/en/latest/capabilities-reference.html?highlight=capabilities
		capability "Switch"
        capability "Switch Level"
        capability "Color"
        capability "Color Temperature"
        capability "Color Mode"
        capability "Color Control"
        capability "Notification"
	}
    tiles(scale: 2) {
        standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: false) {
            state "off", label: '${name}', action: "switch.on", icon: "st.Lighting.light13", backgroundColor: "#ffffff"
            state "on", label: '${name}', action: "switch.off", icon: "st.Lighting.light13", backgroundColor: "#79b821", iconColor: "#ffffff"
        }
        controlTile("levelSliderControl", "device.level", "slider", height: 1, width: 2, inactiveLabel: false, range:"(0..100)") {
            state "level", label: '${currentValue} %', action:"switch level.setLevel"
        }
        controlTile("rgbSelector", "device.color", "color", height: 6, width: 6, inactiveLabel: false) {
            state "color", action: "color control.setColor"
        }
        standardTile("red", "device.red", width: 1, height: 1, canChangeIcon: false) {
            state "red", label: '${name}', action: "red", icon: "st.Lighting.light13", backgroundColor: "#ff0000", iconColor: "#ff0000"
        }
        standardTile("blue", "device.blue", width: 1, height: 1, canChangeIcon: false) {
            state "blue", label: '${name}', action: "blue", icon: "st.Lighting.light13", backgroundColor: "#0000FF", iconColor: "#0000ff"
        }
        standardTile("rainbow", "device.rainbow", width: 1, height: 1, canChangeIcon: false) {
            state "rainbow", label: '${name}', action: "rainbow", icon: "st.Lighting.light13", backgroundColor: "#d3ffd3", iconColor: "#0000ff"
        }
        standardTile("america", "device.america", width: 1, height: 1, canChangeIcon: false) {
            state "america", label: '${name}', action: "america", icon: "st.Lighting.light13", backgroundColor: "#d3ffd3", iconColor: "#0000ff"
        }
        standardTile("leila", "device.leila", width: 1, height: 1, canChangeIcon: false) {
            state "leila", label: '${name}', action: "leila", icon: "st.Lighting.light13", backgroundColor: "#d3ffd3", iconColor: "#0000ff"
        }

        main(["switch"])

        details(["switch","levelSliderControl","rgbSelector","red","blue","rainbow","america","leila"])
    }
}

// miscellaneous

def parse(String description) {
	log.error "This device does not support incoming events"
	return null
}

def installed() {

}

// switch commands

def off(){
	setLevel 0
}

def on() {
	setLevel 100
}

// switch level commands

def setLevel(level) {
	//TODO: support "rate"
   	particle_setBrightness level
}

// color control commands

//color: COLOR_MAP (object)
//def setColor(color) { 
//	particle_setColor color
//}

//hue: PositiveInteger
//def setHue(hue) {
//
//}

//saturation: PositiveInteger
//def setSaturation(saturation) {
//
//}

//def setColorValue(color) { 
//	particle_setColor color
//}

// custom commands

command "red"
def red() {
	particle_setColor 'red'
}

command "blue"
def blue() {
	particle_setColor 'blue'
}

command "rainbow"
def rainbow() {
	particle_setMode 'rainbow'
}

command "america"
def america() {
	particle_setMode 'america'
}

command "leila"
def leila() {
	particle_setMode 'leila'
}

// particle api

def particle_setBrightness(brightness) {
    post 'setBrightness', brightness
}

def particle_setColor(color) {
    post 'setColor', color
}

def particle_setMode(mode) {
    post 'setMode', mode
}

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