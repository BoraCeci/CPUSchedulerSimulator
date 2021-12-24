//Borana Ceci
//CMP 426 OPERATING SYSTEM
//PROF. STEVEN FULAKEZA

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class CPUScheduler{
	public static void main(String[] args){
		String fileName=args[0];
		int quantumTime=Integer.parseInt(args[1]);
		String text="";
		Scanner inStream=null;
		
		//array to store processes.

		process[] processArray;
		
		int currentTime=0;
		
		//to store total tt,tw,tr for SJF.
		float ttTimeSJF=0;
		float twTimeSJF=0;
		float trTimeSJF=0;
		
		//to store total tt,tw,tr for RR.
		float ttTimeRR=0;
		float twTimeRR=0;
		float trTimeRR=0;
		
		
		//reading the file and getting the processes.

		try{
			File file=new File(fileName);
			inStream=new Scanner(file);
			while(inStream.hasNextLine()){
				//we adding a comma (,) in between two processes so we can split them later.
				text=text+inStream.nextLine()+",";
			}
		}
		catch(FileNotFoundException e){
			System.out.println("File not found.");
		}
		inStream.close();
		
		//dividing the text by "," and putting it in an array, index by index.
		String[] processesFromText=text.split(",");
		
		processArray=new process[processesFromText.length];
		
		for(int i=0;i<processesFromText.length;i++){
			String[] processArguements=processesFromText[i].split(" ");
			String id=processArguements[0];
			int a=Integer.parseInt(processArguements[1]);
			int b=Integer.parseInt(processArguements[2]);
			processArray[i]=new process(id,a,b);
		}
		//now we have a processes array, where we have all the processes saved that we got from the file.
		
		sort(processArray);
		System.out.println("\n-------------------------------------------------");
		System.out.println("\t\tCPU Scheduling Simulator");
		System.out.println("-------------------------------------------------\n");
		System.out.println("-------------------------------------------------");
		System.out.println("\tShortest Job First Served Scheduling");
		System.out.println("-------------------------------------------------\n");
		
			// this is just to track how many more processes still need to run.
			int totalProcess = processArray.length;
			process ready[] = new process[totalProcess];	//An array to store the processes that are ready to run.
			int shortestp = 0;
			int numOfReadyProcess=0;
			
			while(totalProcess > 0) {
				
				// loop to check what processes are ready and putting them in the ready array.
				for (int p=0; p<processArray.length; p++) {
					
						if (processArray[p].aTime<=currentTime && processArray[p].status==true) {
							ready[p]= processArray[p];
							processArray[p].status=false;
							numOfReadyProcess++;
						}
						
				}
				
				if(numOfReadyProcess>0){
					
					// loop to check shortest processes from the ready array.
					for (int p=0; p<ready.length; p++) {
					
						if (ready[p]!= null) {
							if(ready[shortestp]!=null){
								if (ready[p].bTime<ready[shortestp].bTime) {
									shortestp = p;
								}
							}
							else{
								shortestp=p;
							}
						}
					}
					// printing the time interval for the shortest ready process. 
					String id=ready[shortestp].id;
					int a=ready[shortestp].aTime;
					int b=ready[shortestp].bTime;
					System.out.println("["+currentTime+"-"+(currentTime+b)+"]\t"+id+" running");
					//we are calculating the tt,wt,rt etc.
					ready[shortestp].tTime=currentTime+b-a;
					ready[shortestp].wTime=currentTime-a;
					ready[shortestp].rTime=currentTime-a;
					ttTimeSJF=ttTimeSJF+ready[shortestp].tTime;
					twTimeSJF=twTimeSJF+ready[shortestp].wTime;
					trTimeSJF=trTimeSJF+ready[shortestp].rTime;
					//updating the current time and all.
					currentTime=currentTime+b;
					totalProcess--;
					numOfReadyProcess--;
					ready[shortestp]=null;
					
			
				}
				
				//means we have still more processes to run but none is ready so from here no 
				//process will run and we need to update current time
				else{
					int closestp=0;
					for (int p=0; p<processArray.length; p++) {
						// finding the next closest process from the current time.
						if (processArray[p].status==true) {
							if(processArray[closestp].status==true){
								if (processArray[p].aTime<processArray[closestp].aTime) {
									closestp = p;
								}
							}
							else{
								closestp=p;
							}
						}
					}
					//print time interval for IDLE.
					System.out.println("["+currentTime+"-"+(processArray[closestp].aTime)+"]\t"+"Idle");
					currentTime=processArray[closestp].aTime;
					
				}
			
			}
		
		System.out.println("\nTurnaround times:");
		
		for(int i=0;i<processArray.length;i++){
			System.out.println("\t"+processArray[i].id+" = "+processArray[i].tTime);
		}
		
		System.out.println("Wait times:");
		
		for(int i=0;i<processArray.length;i++){
			System.out.println("\t"+processArray[i].id+" = "+processArray[i].wTime);
		}
		
		System.out.println("Response times:");
		
		for(int i=0;i<processArray.length;i++){
			System.out.println("\t"+processArray[i].id+" = "+processArray[i].rTime);
		}
		System.out.println();
		System.out.println("Average Turn around time: "+ttTimeSJF/processArray.length);
		System.out.println("Average Wait time: "+twTimeSJF/processArray.length);
		System.out.println("Average Response time: "+trTimeSJF/processArray.length);
		
		//setting the current time to 0 for RR.
		currentTime=0;
		
		//setting the total processes need to run.
		totalProcess=processArray.length;
		

		System.out.println("\n-------------------------------------------------");
		System.out.println("\t\tRound Robin Scheduling");
		System.out.println("-------------------------------------------------\n");
		
		while(totalProcess>0){
			//this variable is just to track if some process ran or not.
			boolean pRan=false;
			for(int i=0;i<processArray.length;i++){
				
				//if the process has came for the first time we calculate the response time.
				if(processArray[i].track==processArray[i].bTime){
					processArray[i].rTime2=currentTime-processArray[i].aTime;
				}
				if(processArray[i].track>=quantumTime && processArray[i].aTime<=currentTime){
					System.out.println("["+currentTime+"-"+(currentTime+quantumTime)+"]\t"+processArray[i].id+" running");
					processArray[i].track=processArray[i].track-quantumTime;
					currentTime=currentTime+quantumTime;
					pRan=true;
					
					//if process has finished we calculte rest of the times.
					if(processArray[i].track<=0){
						processArray[i].tTime2=currentTime-processArray[i].aTime;
						processArray[i].wTime2=processArray[i].tTime2-processArray[i].bTime;
						ttTimeRR=ttTimeRR+processArray[i].tTime2;
						twTimeRR=twTimeRR+processArray[i].wTime2;
						totalProcess--;
					}
				}
				//if process needs to still run but less than quantumTime.
				else if(processArray[i].track<quantumTime && processArray[i].track>0 && processArray[i].aTime<=currentTime){
					System.out.println("["+currentTime+"-"+(currentTime+processArray[i].track)+"]\t"+processArray[i].id+" running");
					currentTime=currentTime+processArray[i].track;
					processArray[i].track=0;
					pRan=true;
					
					//process is definitely finished now so we calculate the rest of the times.
					processArray[i].tTime2=currentTime-processArray[i].aTime;
					processArray[i].wTime2=processArray[i].tTime2-processArray[i].bTime;
					ttTimeRR=ttTimeRR+processArray[i].tTime2;
					twTimeRR=twTimeRR+processArray[i].wTime2;
					totalProcess--;
				}
			}
			//if no process ran we need to change our current time.
			if(pRan==false){
				int closestp=0;
				for (int p=0; p<processArray.length; p++) {
					// finding the next closest process from the current time.
					if (processArray[p].track!=0) {
						if(processArray[closestp].track!=0){
							if (processArray[p].aTime<processArray[closestp].aTime) {
								closestp = p;
							}
						}
						else{
							closestp=p;
						}
					}
				}
				//print time interval for IDLE.
				System.out.println("["+currentTime+"-"+(processArray[closestp].aTime)+"]\t"+"Idle");
				currentTime=processArray[closestp].aTime;
			}
		}
		
		System.out.println("\nTurnaround times:");
		
		for(int i=0;i<processArray.length;i++){
			System.out.println("\t"+processArray[i].id+" = "+processArray[i].tTime2);
		}
		
		System.out.println("Wait times:");
		
		for(int i=0;i<processArray.length;i++){
			System.out.println("\t"+processArray[i].id+" = "+processArray[i].wTime2);
		}
		
		System.out.println("Response times:");
		
		for(int i=0;i<processArray.length;i++){
			System.out.println("\t"+processArray[i].id+" = "+processArray[i].rTime2);
			trTimeRR=trTimeRR+processArray[i].rTime2;
		}

		System.out.println("\nAverage Turn around time: "+ttTimeRR/processArray.length);
		System.out.println("Average Wait time: "+twTimeRR/processArray.length);
		System.out.println("Average Response time: "+trTimeRR/processArray.length);
		
		System.out.println("\n-------------------------------------------------");
		System.out.println("Project done by [Borana Ceci]");
		System.out.println("-------------------------------------------------\n");
		
		
	}
	
	//to sort processes array by the arrival time.
	static void sort(process[] arr){
		int l=arr.length;
		for(int i=1;i<l;i++){
			process temp=arr[i];
			int j=i-1;
			while((j>-1)&&(arr[j].aTime>temp.aTime)){
				arr[j+1]=arr[j];
				j--;
			}
			arr[j+1]=temp;
		}
	}
}

class process{
		String id;
		int aTime;
		int bTime;
		//for SJF.
		int tTime;
		int wTime;
		int rTime;
		//for RR.
		int tTime2;
		int wTime2;
		int rTime2;
		//to check if process has already ran or not.
		boolean status=true;
		
		//Just to check how much more of process needs to run.
		int track;
		
		public process(String id,int a,int b){
			this.id=id;
			aTime=a;
			bTime=b;
			track=bTime;
		}
	}