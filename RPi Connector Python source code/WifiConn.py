#Code for connection from Computer(Algorithms) to RPI
import socket
import sys
import time

class WifiConn(object):
	hostname = ''
	port = 0
	serverSocket = None
	wifiClientHostName = ''
	wifiClient = None
	
	#Constructor
	def __init__(self):
		self.hostname = '192.168.24.1'
		self.port = 5566
		self.serverSocket = None
		self.wifiClient = None
		
	def setupServerSocket(self):
		try:
			#creation of INET, STREAMing socket
			self.serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
			print('Server Socket for PC Connection Created')
			
			#before binding of socket, we allow the socket to be able to reuse Address/Port
			self.serverSocket.setsockopt(socket.SOL_SOCKET,socket.SO_REUSEADDR,1)
			
			#binding of socket to host IP and a port
			self.serverSocket.bind((self.hostname, self.port))
			print('Server Socket has been binded')
			
			#limit Socket connection to 1 client
			self.serverSocket.listen(1)
			print('Waiting for Connection...')
			
			#Setup Server Socket to accept connection
			self.wifiClient, self.wifiClientHostName = self.serverSocket.accept()
			#wifiClientHostName is a 'tuple' object
			print('WifiClient ' + str(self.wifiClientHostName) + ' has Connected!')
			
		except Exception as msg:
			print('WifiConnection Error: ', str(msg))

	def close(self):
		try:
			#Close Server Socket Connection if socket is not None
			if self.serverSocket:
				self.serverSocket.close()
				print('WifiConn Server Socket Closed!')
			#Close Client Socket Connection if socket is not None
			if self.wifiClient:
				self.wifiClient.close()
				print('WifiConn Client Socket Closed!')
		except Exception as msg:
			print('Socket Cleanup Error: ' + str(msg))

	def readFromClient(self):
		try:
			#read from Client Socket with the size of 2048 buffer  
			rcvMsg = self.wifiClient.recv(2048)
			return rcvMsg
		except Exception as msg:
			print('Error in reading from Client' + str(msg)) 
			
	def writeToClient(self,commandMsg):
		try:
			#Encode message and send to client
			self.wifiClient.sendto(commandMsg.encode(), self.wifiClientHostName)
		except Exception as msg:
			print('Error in writing to Client' + str(msg)) 
		
if __name__ == "__main__":
	WifiConn = WifiConn()
	WifiConn.setupServerSocket()
	while True:
		msg = WifiConn.readFromClient()
		command = msg[0:1]
		print(command)
		print('From PC: ' + str(msg))
		if command == 'A':
			WifiConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to Arduino!')
		elif command == 'T':
			WifiConn.writeToClient('RPi3: Instruction ' + str(msg[1:]) + ' Sent to Android Tablet!')
		elif msg == ';':
			WifiConn.writeToClient('RPi3: connection Closed!')
			break
		else :
			WifiConn.writeToClient('RPi3: Msg Received!')
	WifiConn.close()
	
	
				
		
