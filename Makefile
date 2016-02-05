mud:
	javac lib/World.java; \
	javac lib/Edge.java; \
	javac lib/Vertex.java; \
	javac lib/Server.java; \
	javac lib/ServerImpl.java; \
	javac lib/ServerInterface.java; \
	javac lib/Client.java; \
	javac lib/ClientImpl.java; \
	javac lib/ClientInterface.java; \

clean:
	cd lib; \
	rm *.class; \
	cd ..
