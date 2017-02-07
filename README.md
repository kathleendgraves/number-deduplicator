# number-deduplicator
The DeduplicationServer program opens a socket that accepts input in the form of 
nine digit numbers from up to 5 concurrent clients. The numbers are de-duplicated 
and stored in a text file upon completion of the program. Any invalid input (eg: strings,
numbers that are not nine digits) closes the client connection. Any client
that enters "terminate" will disconnect all clients and end the entire program.
