import socket
import sys

class WifiClient(object):
	#Constructor
	def __init__(self):
		self.serverAddress = ('192.168.24.1', 5566)
		self.clientSocket = None
		
	def connectToServer(self):
		# creation of socket
		self.clientSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		
		# connect socket to the port where server is listening
		self.clientSocket.connect(self.serverAddress)
		
	def sendToServer(self,testMsg):
		try:
			self.clientSocket.sendall(testMsg.encode())
		except Exception as msg:
			print('Error in sending to server: ' + str(msg)) 
			
	def readFromServer(self):
		try:
			#read from Client Socket with the size of 2048 buffer  
			rcvMsg = self.clientSocket.recv(1024)
			return rcvMsg
		except Exception as msg:
			print('Error in reading from Client' + str(msg)) 		
			
	def close(self):
		try:
			#Close Server Socket Connection if socket is not None
			if self.clientSocket:
				self.clientSocket.close()
				print('WifiConn Server Socket Closed!')
		except Exception as msg:
			print('Socket Cleanup Error: ' + str(msg))
			
if __name__ == "__main__":
	WifiClient = WifiClient()
	WifiClient.connectToServer()
	while True:
		msg = input()
		WifiClient.sendToServer(msg)
		recvMsg = WifiClient.readFromServer()
		print(recvMsg)
		if msg == ';':
			break
	WifiClient.close()
