import sys
import time
import threading
from BTConn import *
from SerConn import *
from WifiConn import *

class RPIInit(threading.Thread):
	def __init__(self):
		#Subclass overrides base class totally, therefore we have to initialise the thread class as well
		threading.Thread.__init__(self)
		
		#Init Connection objects
		self.SerConn = SerConn()
		self.WifiConn = WifiConn()
		self.BTConn = BTConn()
		
		#Init Connections and listen to Wifi -> Bluetooth -> Serial
		self.WifiConn.setupServerSocket()
		self.wifiIsConnected = True
		self.BTConn.setupBTSocket()		
		self.bluetoothIsConnected = True
		self.serialIsConnected = True
		
	def readFromWifiConn(self):
		while True:
			try:
				msg = self.WifiConn.readFromClient()
				#connection lost
				if msg == '': 
					print('Wifi Connection is Lost')
					self.wifiIsConnected = False
					break
					
				#Command Directive Logic for string parsing here		
					
				command = msg[0:1]
				print(command)
				print('From PC: ' + str(msg))
				if command == 'A':
					#From PC to Arduino
					self.WifiConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to Arduino!')
				elif command == 'T':
					#From PC to Tablet
					self.BTConn.writeToClient(str(msg[1:]))
				elif msg == ';':
					self.WifiConn.writeToClient('RPi3: connection Closed!')
					self.wifiIsConnected = False
					break
				else :
					self.WifiConn.writeToClient('RPi3: Msg Received!')
			except Exception as msg:
				print('readFromWifiConn Error' + str(msg)) 
		
	def readFromBTConn(self):
		while True:
			try:
				msg = self.BTConn.readFromClient()
				#connection lost
				if msg == '':
					print('Bluetooth Connection is Lost')
					self.bluetoothIsConnected = False
					break
					
				#Command Directive Logic for string parsing here	
					
				command = msg[0:1]
				print(command)
				print('From BT: ' + str(msg))
				if command == 'P':
					#From Tablet To PC
					self.WifiConn.writeToClient(str(msg[1:]))
				elif command == 'A':
					#Fom Tablet to Arduino
					self.BTConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to Arduino!')
				elif msg == ';':
					self.BTConn.writeToClient('RPi3: connection Closed!')
					self.bluetoothIsConnected = False
					break
				else :
					self.BTConn.writeToClient('RPi3: Msg Received!')
			except Exception as msg:
				print('readFromBTConn Error' + str(msg)) 
		
	def readFromSerialConn(self):
		while True:
			try:
				msg = self.BTConn.readFromClient()
				#connection lost
				if msg == '':
					print('Serial Connection is Lost')
					self.bluetoothIsConnected = False
					break
				command = msg[0:1]
				print(command)
				print('From BT: ' + str(msg))
				if command == 'P':
					#From BT To PC
					self.WifiConn.writeToClient(str(msg[1:]))
				elif command == 'A':
					#Fom BT to Arduino
					self.BTConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to Arduino!')
				elif msg == ';':
					self.BTConn.writeToClient('RPi3: connection Closed!')
					self.bluetoothIsConnected = False
					break
				else :
					self.BTConn.writeToClient('RPi3: Msg Received!')
				pass
			except Exception as msg:
				print('readFromSerialConn Error' + str(msg)) 		
		
	def startThreads(self):
		#Creation of worker Threads
		bluetoothReadThread = threading.Thread(target = self.readFromBTConn, name="Bluetooth Read Thread")
		#serialReadThread = threading.Thread(target = self.readFromSerialConn, name="Serial Read Thread")
		
		
		#start all worker Threads
		bluetoothReadThread.start()
		#serialReadThread.start()
		
	def cleanup(self):
		try:
			self.WifiConn.close()
			self.BTConn.close()
			self.serConn.close()
		except Exception as msg :
			print("Final CleanUp Error! : ", str(msg))	
						
		
if __name__ == "__main__":
	print threading.currentThread().getName(), 'Starting'
	
	RPIInit = RPIInit()
	RPIInit.startThreads()
	#MainThread Does the job of reading from Wifi Connection
	RPIInit.readFromWifiConn()
	#wait until all connections are closed from the other end before cleanup
	print("Awaiting all connections to be closed")
	while True:
		time.sleep(0.5)
		if (not RPIInit.wifiIsConnected) and (not RPIInit.bluetoothIsConnected):
			RPIInit.cleanup()
			break
	
				
		
