
// event representation
class Event implements Comparable<Object> {

 public Event(int a_type, double a_time) { _type = a_type; time = a_time; setFirstDelay(0); setSecondDelay(0);setServiceTime(0);setResponse1(0); setResponse2(0); }
  
 public double time;
 private int _type;
 private double firstDelay; //First delay of a caller
 private double secondDelay; //Second delay of a caller
 private double serviceTime; //Service time taken by a caller  
 private double response1;  //First response  time of a caller
 private double response2;  //Second response  time of a caller
 public int get_type() { return _type; }
 public double get_time() { return time; }

 public Event leftlink, rightlink, uplink;

 public int compareTo(Object _cmpEvent ) {
  double _cmp_time = ((Event) _cmpEvent).get_time() ;
  if( this.time < _cmp_time) return -1;
  if( this.time == _cmp_time) return 0;
  return 1;
 }

 /**
 * Every functions below does the job what their names tell
 */
public double getFirstDelay() {
	return firstDelay;
}
public void setFirstDelay(double firstDelay) {
	this.firstDelay = firstDelay;
}
public double getTotalDelay() {
	return secondDelay+firstDelay;
}
public void setSecondDelay(double secondDelay) {
	this.secondDelay = secondDelay;
}
public double getServiceTime() {
	return serviceTime;
}
public void setServiceTime(double serviceTime) {
	this.serviceTime = serviceTime;
}
public void setResponse1(double response1) {
	this.response1 = response1;
}
public double getTotalResponse() {
	return response2 + response1;
}
public void setResponse2(double response2) {
	this.response2 = response2;
}
public double getResponse1() {
	return response1;
}
};
