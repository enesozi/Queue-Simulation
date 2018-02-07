
import java.util.Random;
/**
* This class is for simulating a call center experiment which includes 
* two operators. The procedure consists of two steps.
* First, a caller takes service from front-desk operator then from expert operator.
* General Clarification : '2' appended to a variable means that it is used for expert operator events.
* For example, TotalBusy2 is of expert operator while TotalBusy is of front-desk operator.
*/
class Sim {

// Class Sim variables
public static double Clock,MeanInterArrivalTime,MeanServiceTime2, MeanServiceTime, SIGMA, LastEventTime,LastEventTime2,
        TotalBusy,TotalBusy2, SumResponseTime,SumResponseTime2,TotalSystemTime,MaxWAitingTime,TotalWTForBothOperators,MaxWAitingTimeToSystemTime;
public static long  NumberOfCustomers, QueueLength,QueueLength2, NumberInService,NumberInService2,
        TotalCustomers, NumberOfDepartures,CustNumWaitingExpert,TotalNumberOfCustWaited,MaxQueueLength,MaxQueueLength2;

public final static int arrival = 1;
public final static int departure = 2;

public static EventList FutureEventList;
public static EventList FutureEventList2;
public static Queue Customers;
public static Queue Customers2;
public static Random stream;
 

public static void main(String argv[]) {

  MeanInterArrivalTime = 5.2; MeanServiceTime = 3.2;
  MeanServiceTime2 = 4.5; 
  SIGMA                = 0.4; TotalCustomers  = Long.parseLong(argv[1]);
  long seed            = Long.parseLong(argv[0]);

  stream = new Random(seed);           // initialize rng stream
  FutureEventList = new EventList();
  FutureEventList2 = new EventList();
  Customers = new Queue();
  Customers2 = new Queue();
  Initialization();

  // Loop until # of departures is equal to  TotalCustomers given as second argument.
    while(NumberOfDepartures < TotalCustomers) {
	// Next 6 lines are for determining which event is more imminent
	// i.e is to determine which operator is going to serve her/his caller.   
   	double e2 = Double.MAX_VALUE;
   	double e1 = Double.MAX_VALUE;
   	if(!FutureEventList2.isEmpty())
   	 e2 = ((Event)FutureEventList2.getMin()).get_time();
   if(!FutureEventList.isEmpty())
   	 e1 = ((Event)FutureEventList.getMin()).get_time();
    if( e2 <= e1){
    	Event evt2 = (Event)FutureEventList2.getMin();  // get imminent event
        FutureEventList2.dequeue();                    // be rid of it
        Clock = evt2.get_time();                       // advance simulation time
        if( evt2.get_type() == arrival) ProcessArrival2(evt2);
        else if(evt2.get_type() == departure)  ProcessDeparture2(evt2);
        //System.out.println("Clock is "+Clock);
    }
    if(e1 <= e2){
    Event evt = (Event)FutureEventList.getMin();  // get imminent event
    FutureEventList.dequeue();                    // be rid of it
    Clock = evt.get_time();                       // advance simulation time
    if( evt.get_type() == arrival) ProcessArrival(evt);
    else if(evt.get_type() == departure)  ProcessDeparture(evt);
    //System.out.println("Clock is "+Clock);
    }
    }
  ReportGeneration();
 }

/**
* This function is to process departure of an event from expert operator.
* Again if there is someone in the queue schedule new departure 
* And again record the informations obtained from the finished event.
*/
 private static void ProcessDeparture2(Event evt) {
	 // get the customer description
	 Event finished = (Event) Customers2.dequeue();
	 // if there are customers in the queue then schedule
	 // the departure of the next one
	   
	  if( QueueLength2 > 0 ) {
		  Event nextEv = (Event) Customers2.peekFront();
		  ScheduleDeparture2(nextEv.getFirstDelay(),nextEv.getResponse1());
		  }
	  else NumberInService2 = 0;
	  // measure the response time and add to the sum
	  double response = (Clock - finished.get_time());
	  // waiting time in the second queue
	  double secondDelay = Clock - (finished.get_time() + evt.getServiceTime());
	  evt.setSecondDelay(secondDelay);
	  evt.setResponse2(response);
	    // if second delay exists increment the number of customers waiting for the expert operator
	      if(secondDelay>0) CustNumWaitingExpert++;
	    // if a caller waits for at least one operator then increment the total # of customers waiting.
	      double totalDelay = evt.getTotalDelay(); 
	      if(totalDelay > 0) TotalNumberOfCustWaited++;
	    // update max waiting time to system time ratio
	      double totalResponse = evt.getTotalResponse();
	      double maxTotalWTtoTotalRt = totalDelay/totalResponse;
	      if(MaxWAitingTimeToSystemTime < maxTotalWTtoTotalRt) MaxWAitingTimeToSystemTime = maxTotalWTtoTotalRt;
		  if(MaxWAitingTime < totalDelay ) MaxWAitingTime=totalDelay;
		  TotalWTForBothOperators+=secondDelay;
		 // System.out.println(maxTotalWTtoTotalRt);
	  SumResponseTime2 += response;
	  TotalBusy2 += (Clock - LastEventTime2 );

	  LastEventTime2 = Clock;
	  // this is where departure occurs that are counted 
	  NumberOfDepartures++;
}

/**
* This function is to process arrival event 
* if there is someone in the queue , schedule new departure.
* But arrivals are of the callers departing from front-desk operator.
*/
private static void ProcessArrival2(Event evt) {
	 Customers2.enqueue(evt); 
	  QueueLength2++;
	  // if the server is idle, fetch the event, do statistics
	  // and put into service
	  if( NumberInService2 == 0) ScheduleDeparture2(evt.getFirstDelay(),evt.getResponse1());
	  else TotalBusy2 += (Clock - LastEventTime2);  // server is busy

	  // adjust max queue length statistics
	  if (MaxQueueLength2 < QueueLength2) MaxQueueLength2 = QueueLength2;

	  LastEventTime2 = Clock;
}
/**
* @param d : first delay of the event which is sent for departing by expert operator.
* The same process as in the first part of the simulation.
 * @param r 
*/
private static void ScheduleDeparture2(double d, double r) {
	// TODO Auto-generated method stub
	 double ServiceTime;
	  // get the job at the head of the queue
	  while (( ServiceTime = exponential(stream, MeanServiceTime2)) < 0 );
	  Event depart = new Event(departure,Clock+ServiceTime);
	  depart.setServiceTime(ServiceTime);
	  depart.setFirstDelay(d);
	  depart.setResponse1(r);
	  FutureEventList2.enqueue( depart );
	  NumberInService2 = 1;
	  QueueLength2--;
}

// seed the event list with TotalCustomers arrivals
 public static void Initialization()   { 
  Clock  = 0.0;
  Clock = 0.0;
  QueueLength = 0;
  QueueLength2 = 0;
  NumberInService = 0;
  NumberInService2 = 0;
  LastEventTime = 0.0;
  LastEventTime2 = 0.0;
  TotalBusy = 0 ;
  TotalBusy2 = 0 ;
  MaxQueueLength = 0;
  MaxQueueLength2 = 0;
  SumResponseTime = 0;
  SumResponseTime2 = 0;
  NumberOfDepartures = 0;
  TotalSystemTime = 0;
  TotalWTForBothOperators = 0;
  CustNumWaitingExpert = 0;
  TotalNumberOfCustWaited = 0;
  MaxWAitingTimeToSystemTime = 0;
  // create first arrival event
  Event evt = new Event(arrival, exponential( stream, MeanInterArrivalTime));
  FutureEventList.enqueue( evt );
 }

/**
* This function is to process arrival event 
* If there is someone in the queue , schedule new departure.
*/
 public static void ProcessArrival(Event evt) {
  Customers.enqueue(evt); 
  QueueLength++;
  // if the server is idle, fetch the event, do statistics
  // and put into service
  if( NumberInService == 0) ScheduleDeparture();
  else TotalBusy += (Clock - LastEventTime);  // server is busy

  // adjust max queue length statistics
  if (MaxQueueLength < QueueLength) MaxQueueLength = QueueLength;

  // schedule the next arrival

  Event next_arrival = new Event(arrival, Clock+exponential(stream, MeanInterArrivalTime));
  FutureEventList.enqueue( next_arrival );
  
  LastEventTime = Clock;
 }

/**
* This function is to schedule departure event 
* Update field of the event
*/
 public static void ScheduleDeparture() {
  double ServiceTime;
  // get the job at the head of the queue
  while (( ServiceTime = normal(stream, MeanServiceTime, SIGMA)) < 0 );
  Event depart = new Event(departure,Clock+ServiceTime);
  // save service time given to the event 
  // since I'll use this info for calculating Total Waiting Time
  depart.setServiceTime(ServiceTime);
  FutureEventList.enqueue( depart );
  NumberInService = 1;
  QueueLength--;
 }


/**
* This function is to process departure event 
* If there is someone in the queue , schedule new departure.
* Then before caller depart , record the infos to utilize them in ReportGeneration
* And The caller departing is sent to expert operator with an event of arrival
*/
public static void ProcessDeparture(Event e) {
 // get the customer description
 Event finished = (Event) Customers.dequeue();
 // if there are customers in the queue then schedule
 // the departure of the next one
  if( QueueLength > 0 ) ScheduleDeparture();
  else NumberInService = 0;
  // measure the response time and add to the sum
  double response = (Clock - finished.get_time());
  // First Delay represents the time spent in the queue
  double firstDelay = Clock - (finished.get_time()+e.getServiceTime());
  SumResponseTime += response;
  TotalBusy += (Clock - LastEventTime );
  TotalWTForBothOperators+=firstDelay;
  LastEventTime = Clock;
  Event newEvent = new Event(arrival, Clock);
  // Set the first delay to get the correct value in ProcessDeparture2
  newEvent.setFirstDelay(firstDelay);
  newEvent.setResponse1(response);
  FutureEventList2.enqueue(newEvent);
 }

/**
* This function is to generate a report about simulation
*/
public static void ReportGeneration() {
	TotalSystemTime = SumResponseTime+SumResponseTime2;
	double utilFront = TotalBusy/Clock;
	double utilExpert = TotalBusy2/Clock;
	double averageTotalWaiting = 0.0;
	if(TotalNumberOfCustWaited != 0) averageTotalWaiting = TotalWTForBothOperators/TotalNumberOfCustWaited;

System.out.println( "\n\tTWO SERVERS QUEUE SIMULATION:  ");
System.out.println(	"\t-----------------------------  \n");
System.out.println( "\tUtilization of the front-desk operator:                           %" + utilFront*100 );
System.out.println( "\tUtilization of the expert operator:                               %" + utilExpert*100 );
System.out.println( "\tAverage Total Waiting Time:            			           "        + averageTotalWaiting );
System.out.println( "\tMaximum Total Waiting Time to Total System Time Ratio:            %" + MaxWAitingTimeToSystemTime*100 );
System.out.println( "\tTotal Number of Customers who Waited for the Expert Operator:	   "    + CustNumWaitingExpert);
//System.out.println( "\tMaximum Queue Length For First Operator: 			     "          + MaxQueueLength);
//System.out.println( "\tMaximum Queue Length For Expert Operator: 			     "          + MaxQueueLength2);
//System.out.println( "\tMaximum Waiting Time: 						   "          +MaxWAitingTime);
System.out.println( "\tNUMBER OF DEPARTURES:                                              " + NumberOfDepartures );
}

public static double exponential(Random rng, double mean) {
 return -mean*Math.log( rng.nextDouble() );
}

public static double SaveNormal;
public static int  NumNormals = 0;
public static final double  PI = 3.1415927 ;

public static double normal(Random rng, double mean, double sigma) {
        double ReturnNormal;
        // should we generate two normals?
        if(NumNormals == 0 ) {
          double r1 = rng.nextDouble();
          double r2 = rng.nextDouble();
          ReturnNormal = Math.sqrt(-2*Math.log(r1))*Math.cos(2*PI*r2);
          SaveNormal   = Math.sqrt(-2*Math.log(r1))*Math.sin(2*PI*r2);
          NumNormals = 1;
        } else {
          NumNormals = 0;
          ReturnNormal = SaveNormal;
        }
        return ReturnNormal*sigma + mean ;
 }
}

