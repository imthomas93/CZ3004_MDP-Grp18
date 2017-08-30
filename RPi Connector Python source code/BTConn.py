#!/usr/bin
import serial
from bluetooth import *

class BTConn(object):
	def __init__(self):		
		self.bluetoothPort = 4
		self.bluetoothSocket = None
		
	def setupBTSocket(self):
		try:
			#Creation of Bluetooth Socket
			self.bluetoothSocket = BluetoothSocket(RFCOMM)
			print('Bluetooth Socket = RFCOMM created')
			
			#Binding of Bluetooth Socket
			self.bluetoothSocket.bind(("",self.bluetoothPort))
			print('Bluetooth Socket Binded')
			
			#Allow only 1 device to be connected to Bluetooth Socket
			self.bluetoothSocket.listen(1)
			port = self.bluetoothSocket.getsockname()[1]
			
			uuid = "94f39d29-7d6d-437d-973b-fba39e49d4ee"
			advertise_service( self.bluetoothSocket, "MDP-Server",
			 service_id = uuid,
			 service_classes = [ uuid, SERIAL_PORT_CLASS ],
			 profiles = [ SERIAL_PORT_PROFILE ],
			# protocols = [ OBEX_UUID ]
			 )
			
			#Set Bluetooth Socket to accept incoming connection 
			print("Waiting for connection on RFCOMM channel %d" % port)
			self.clientSocket, self.client_info = self.bluetoothSocket.accept()
			print("Accepted connection from ", self.client_info)	
		except Exception as msg:
			print('Error in Setting up: ' + str(msg))
		
	def readFromClient(self):
		try:
			recvMsg = self.clientSocket.recv(2048)
			return recvMsg
		except Exception as msg:
			print('Error in reading: ' + str(msg))
			
	def writeToClient(self,command):
		try:
			self.clientSocket.send(str(command))	
		except Exception as msg:
			print('Error in writing: ' + str(msg))					
		
	def close(self):
		try: 
			if self.clientSocket:
				self.clientSocket.close()
				print('Client Socket Closed')
			if self.bluetoothSocket:
				self.bluetoothSocket.close()
				print('Bluetooth Socket Closed')
		except Exception as msg:
			print('Error in cleanup: ' + str(msg))

if __name__ == "__main__" :
	BTConn = BTConn()
	BTConn.setupBTSocket()
	#TEST CODE
	while True:
		msg = BTConn.readFromClient()
		command = msg[0:1]
		print(command)
		print('From PC: ' + str(msg))
		if command == 'P':
			BTConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to PC!')
		elif command == 'A':
			BTConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to Arduino!')
		#Close Connection	
		elif msg == ';':
			BTConn.writeToClient('RPi3: connection Closed!')
			break
		else :
			BTConn.writeToClient('RPi3: Msg Received!')
	BTConn.close()
	
