public class Process {
    private String name;
    private String color;
    private int arrivalTime;
    private int burstTime;
    private int priorityNumber;
    private int completionTime;

    public Process(String name, String color, int arrivalTime, int burstTime, int priorityNumber){
        this.setName(name);
        this.setColor(color);
        this.setArrivalTime(arrivalTime);
        this.setBurstTime(burstTime);
        this.setPriorityNumber(priorityNumber);
    }

    public void setName(String name){
        this.name = name;
    }
    public void setColor(String color){
        this.color = color;
    }
    public void setArrivalTime(int arrivalTime){
        this.arrivalTime = arrivalTime;
    }
    public void setBurstTime(int burstTime){
        this.burstTime = burstTime;
    }
    public void setPriorityNumber(int priorityNumber){
        this.priorityNumber = priorityNumber;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public String getName(){
        return this.name;
    }
    public String getColor(){
        return this.color;
    }
    public int getArrivalTime(){
        return this.arrivalTime;
    }
    public int getBurstTime(){
        return this.burstTime;
    }
    public int getPriorityNumber(){
        return this.priorityNumber;
    }

    public int getCompletionTime() {
        return completionTime;
    }
}