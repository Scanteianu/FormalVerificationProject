#usage: python3 holyUnroller.py <inputFile(ie: reach3.txt)> <number of unrollings (ie: 10)>; requires minisat imported
#this was named after the holy roller in the Beatles song "Come Together"
import os
import sys
count=0
def translate(x,dict):
	if(x>=0):
		return dict[x]
	else:
		return -1*dict[-1*x]
def buildAdjList(adjListVar,edgesVar,nodeNumVar):
	for i in range(nodeNumVar):
		adjListVar.append([])
	for edge in edgesVar:
		adjListVar[edge[0]].append(edge[1])
edges=[]
unrolledEdges=[]
adjList=[]
revAdjList=[]
cnf=[]
numIter=int(sys.argv[2])
nodeNum=-1
with open(sys.argv[1],"r") as reach:
	for row in reach:
		if(nodeNum==-1):
			nodeNum=int(row.split(" ")[0])
			#numIter=int(row.split(" ")[1])
		#print(row)
		else:
			edgeStr=row.split(" ")
			edges.append((int(edgeStr[0]),int(edgeStr[1])))
if(len(edges)<20):
	for edge in edges:
		print(edge)
revEdge=[(y,x)for(x,y) in edges]
#build adj list
buildAdjList(adjList,edges,nodeNum)
buildAdjList(revAdjList,revEdge,nodeNum)
#print(adjList)
print(revAdjList)
#unroll
startClause=[]
badClause=[]
for i in range(0,numIter):
	for edge in edges:
		unrolledEdges.append(((i*nodeNum)+edge[0],((i+1)*nodeNum)+edge[1]))
	#here is the cnf magic
	for j in range(len(adjList)):
		if(revAdjList[j]!=[]):
			clause=[]
			
			#either dest is not reachable, or some of its srcs are reachable
			clause.append(-1*(j+(i+1)*nodeNum))
			for src in revAdjList[j]:
				clause.append((i*nodeNum)+src)
			cnf.append(clause)
		if(revAdjList[j]==[]):
			#unreachable
			#print("unreach: "+str(j))
			cnf.append([-1*(j+(i+1)*nodeNum)])
	badClause.append(((i+1)*nodeNum)+1)
#no initial states other than the 
for i in range(1, nodeNum):
	cnf.append([-1*i])
cnf.append(badClause) #the bad state
cnf.append([0]) #the start state must be reached
#remove unused variables
varToReduced={}
reducedCnf=[]
for clause in cnf:
	if(len(cnf)<100):
		print(clause)
	reducedClause=[]
	for var in clause:
		if (abs(var) not in varToReduced):
			count+=1
			varToReduced[abs(var)]=count
		if(var>=0):
			reducedClause.append(translate(var,varToReduced))
		else:
			reducedClause.append(-1*varToReduced[-1*var])
	reducedCnf.append(reducedClause)
reducedToVar={}
#invert relationship for decoding minisat output
for var in varToReduced:
	reducedToVar[varToReduced[var]]=var
#generate file
dimacs=""
dimacs+="p cnf "+str(count)+" "+str(len(reducedCnf))+"\n"
for clause in reducedCnf:
	for var in clause:
		dimacs+=str(var)+" "
	dimacs+="0\n"
#call minisat			
with open("dimacs.txt", "w+") as dimacsFile:
	dimacsFile.write(dimacs)
os.system("minisat dimacs.txt miniout.txt")
#reinterpret result
path=[]
with open("miniout.txt", "r") as miniout:
	for row in miniout:
		if("UNSAT"in row):
			print("not reachable")
			break
		if("SAT" in row):
			pass
		else:
			for token in row.split(" "):
				if(int(token)>0):
					path.append(translate(int(token),reducedToVar))
					path.sort()
					
if(path!=[]):
	print(path)
	print([i %nodeNum for i in path])
# print("\n\n\n\nunrolled\n\n\n")
# for edge in unrolledEdges:
	# print(edge)
	
#http://www.cs.cmu.edu/~emc/15817-s05/bmc.ppt
