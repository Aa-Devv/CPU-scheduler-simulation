import java.io.File; //will be used to turn a text file into an instance of File.
import java.util.Scanner; //will be used to read the file as well as an input from the user.
import java.util.ArrayList; //will be used to store data.


public class CPUscheduler {
    private static ArrayList<Job> jobs = new ArrayList<>(); //an arraylist of the jobs that needs executing.
    private static int CPUtime; //It stores the time since the CPU started executing its first job in the current session.
    private static String algorithmName; //stores the name of the current CPU scheduling algorithm used.
    private static ArrayList<Work> works = new ArrayList<>(); //an arraylist to store the CPU execution timeline.

    public static void main(String[] args) {
        String pathname = "C:\\Users\\Aa-Devv\\Desktop\\Project\\job.txt"; //here you put the location of the txt file.
        System.out.print("Choose the scheduling algorithm (s,p,r): ");
        Scanner s = new Scanner(System.in);
        while (true) {
            String choice = s.nextLine();
            if (choice.equalsIgnoreCase("SRTF") || choice.equalsIgnoreCase("S") || choice.equalsIgnoreCase("Shortest Remaining Time First")) {
                SRTF(pathname);
                return;
            } else if (choice.equalsIgnoreCase("p") || choice.equalsIgnoreCase("priority")) {
                priority(pathname);
                return;
            } else if (choice.equalsIgnoreCase("RR") || choice.equalsIgnoreCase("Round Robin") || choice.equalsIgnoreCase("R")) {
                System.out.print("\n\nEnter quantum size for RoundRobin: ");

                int q = s.nextInt();

                while (true) {
                    if (q > 0) {
                        RoundRobin(q, pathname);
                        return;
                    } else {
                        System.err.print("Quantum size must be bigger than zero. Enter quantum size: ");
                        q = s.nextInt();
                    }
                }
            } else System.err.print("Enter a valid choice between SRTF or Priority or Round Robin: ");
        }
    }

    private static void scanSRTF(String pathname) {
        //reads every line  with the assumption that a line contains three attributes of a single job and each attribute is separated by a comma.
        // beginning with the name of the job then its arrival time and then its burst time.
        StringBuilder str = new StringBuilder();
        try {
            Scanner s = new Scanner(new File(pathname)); //scanner s will be used to read the jobs in the text file in the path.
            while (s.hasNextLine()) str.append(s.nextLine()).append("\n");

        } catch (Exception e) {
            System.err.println("Problem with File.");
            e.printStackTrace();
        }
        for (int i = 0; i < str.length(); i++) {
            Job a = new Job();
            StringBuilder n = new StringBuilder();
            for (; i < str.length() && str.charAt(i) != ',' && str.charAt(i) != '\n'; i++)
                n.append(str.charAt(i));

            a.setName(n.toString());
            if (i < str.length()) i++;

            n = new StringBuilder();
            for (; i < str.length() && str.charAt(i) != ',' && str.charAt(i) != '\n'; i++)
                if (isInt(String.valueOf(str.charAt(i)))) n.append(str.charAt(i));
            if (isInt(n.toString())) a.setArrivalTime(Integer.valueOf(n.toString()));
            if (i < str.length()) i++;

            n = new StringBuilder();
            for (; i < str.length() && str.charAt(i) != ',' && str.charAt(i) != '\n'; i++)
                if (isInt(String.valueOf(str.charAt(i)))) n.append(str.charAt(i));
            if (isInt(n.toString())) a.setBurstTime(Integer.valueOf(n.toString()));
            addJob(a); //sends the job to be added to the arraylist of jobs.

        }
    }

    private static boolean isInt(String s) {
        //checks if the string given as an argument is a valid number.
        try {
            Integer.valueOf(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static void scanPriority(String pathname) {
        //reads every line in an oscillating pattern, with the assumption that every odd line contains the name of the job
        // and every even line contains its the burst time followed by the priority value that is separated by a comma.
        StringBuilder str = new StringBuilder();
        try {
            Scanner s = new Scanner(new File(pathname)); //scanner s will be used to read the jobs in the text file in the path.
            while (s.hasNextLine()) str.append(s.nextLine()).append("\n");

        } catch (Exception e) {
            System.err.println("Problem with File.");
            e.printStackTrace();
        }


        Job a = new Job();
        StringBuilder n = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            for (; i < str.length() && str.charAt(i) != '\n'; i++)
                n.append(str.charAt(i));
            if (i == str.length()) return;
            a.setName(n.toString());
            i++;
            n = new StringBuilder();
            for (; i < str.length() && str.charAt(i) != ',' && str.charAt(i) != '\n'; i++)
                if (isInt(String.valueOf(str.charAt(i)))) n.append(str.charAt(i));
            if (i == str.length()) return;
            if (isInt(n.toString())) a.setBurstTime(Integer.valueOf(n.toString()));
            i++;
            n = new StringBuilder();
            for (; i < str.length() && str.charAt(i) != ',' && str.charAt(i) != '\n'; i++)
                if (isInt(String.valueOf(str.charAt(i)))) n.append(str.charAt(i));

            if (isInt(n.toString())) a.setPriority(Integer.valueOf(n.toString()));
            if (a.getPriority() >= 1) addJob(a);
            if (i == str.length()) return;
            i++;
            a = new Job();
            n = new StringBuilder();
        }


    }

    private static void scanRR(String pathname) {
        //reads every line in an oscillating pattern, with the assumption that every odd line contains the name of the job
        // and every even line contains its the burst time.
        StringBuilder str = new StringBuilder();
        try {
            Scanner s = new Scanner(new File(pathname)); //scanner s will be used to read the jobs in the text file in the path.
            while (s.hasNextLine()) str.append(s.nextLine()).append("\n");

        } catch (Exception e) {
            System.err.println("Problem with File.");
            e.printStackTrace();
        }


        Job a = new Job();
        StringBuilder n = new StringBuilder();
        int i = 0;
        while (i < str.length()) {
            for (; i < str.length() && str.charAt(i) != '\n'; i++)
                n.append(str.charAt(i));
            if (i == str.length()) return;
            a.setName(n.toString());
            i++;
            n = new StringBuilder();

            for (; i < str.length() && str.charAt(i) != ',' && str.charAt(i) != '\n'; i++)
                if (isInt(String.valueOf(str.charAt(i)))) n.append(str.charAt(i));

            if (isInt(n.toString())) a.setBurstTime(Integer.valueOf(n.toString()));
            addJob(a);
            if (i == str.length()) return;
            i++;
            a = new Job();
            n = new StringBuilder();
        }


    }


    private static void addJob(Job a) {
        //adds job 'a' into the arraylist of jobs, if its burst time isn't zero (which means it needs executing) and the arrivaltime is valid.
        if (a.getBurstTime() != 0 && a.getArrivalTime() >= 0) jobs.add(a);


    }

    private static int getIdSmallestBurstTime() {
        //returns the index for the job with the smallest burst time (if the bust time isn't zero) that arrived before or at the current algorithm time, returns -1 when empty or if all jobs have zero bursts.
        int smallest = 0;
        for (int i = 1; i < jobs.size() && jobs.get(i) != null; i++)
            if ((jobs.get(i).getArrivalTime() <= CPUtime && jobs.get(i).getBurstTime() != 0) && (jobs.get(smallest).getBurstTime() == 0 || jobs.get(i).getBurstTime() < jobs.get(smallest).getBurstTime() || (jobs.get(i).getBurstTime() == jobs.get(smallest).getBurstTime() && jobs.get(i).getArrivalTime() < jobs.get(smallest).getArrivalTime()) || jobs.get(smallest).getArrivalTime() > CPUtime))
                smallest = i;

        if (jobs.get(smallest).getBurstTime() > 0 && jobs.get(smallest).getArrivalTime() <= CPUtime)
            return smallest;
        return -1;
    }


    public static void SRTF(String pathname) {
        //schedules jobs to the CPU according to the shortest remaining burst time.
        jobs = new ArrayList<>();
        scanSRTF(pathname);
        algorithmName = "Shortest Remaining Time First scheduling";
        if (jobs.size() == 0) {
            System.err.println("No jobs in the system for " + algorithmName + ".");
            return;
        }
        works = new ArrayList<>();
        CPUtime = 0;
        sort();
        while (maxBurstTime() != 0) {
            while (getIdSmallestBurstTime() == -1) CPUtime++;
            Work w = new Work();
            works.add(w);
            w.setExecutedWork(jobs.get(getIdSmallestBurstTime()));
            w.setBurstTime(w.getExecutedWork().getBurstTime());
            w.setEntryTime(CPUtime);
            while (w.getExecutedWork().getBurstTime() != 0 && jobs.get(getIdSmallestBurstTime()) == w.getExecutedWork()) {
                w.getExecutedWork().decrementBurstTime();
                for (int i = 0; i < jobs.size(); i++)
                    if (jobs.get(i).getBurstTime() != 0 && jobs.get(i).getArrivalTime() <= CPUtime && jobs.get(i) != w.getExecutedWork())
                        jobs.get(i).incrementWaitingTime();
                CPUtime++;
            }
            if (w.getExecutedWork().getBurstTime() == 0) w.setExitTime(CPUtime);
        }
        grid();
    }

    private static boolean jobAvailable() {
        //checks if there are jobs awaiting execution and their arrivalTime is before or at the current algorithmTime.
        for (int i = 0; i < jobs.size(); i++)
            if (jobs.get(i).getBurstTime() != 0 && jobs.get(i).getArrivalTime() <= CPUtime) return true;
        return false;
    }

    public static void RoundRobin(int quantum, String pathname) {
        //schedules jobs to the CPU according to the time of arrival, and each job has the same time of executing (quantum).
        jobs = new ArrayList<>();
        scanRR(pathname);
        algorithmName = "Round Robin scheduling";
        if (jobs.size() == 0) {
            System.err.println("No jobs in the system for " + algorithmName + ".");
            return;
        }
        works = new ArrayList<>();
        CPUtime = 0;
        sort();
        int x = -1;
        while (maxBurstTime() != 0) {
            x = (x + 1) % jobs.size();
            while (!jobAvailable()) CPUtime++;
            if (jobs.get(x).getArrivalTime() <= CPUtime && jobs.get(x).getBurstTime() != 0) {
                Work w = new Work();
                works.add(w);
                w.setExecutedWork(jobs.get(x));
                w.setBurstTime(w.getExecutedWork().getBurstTime());
                w.setEntryTime(CPUtime);
                for (int j = 0; w.getExecutedWork().getBurstTime() != 0 && (j < quantum || jobs.size() == 1); CPUtime++, j++) {
                    w.getExecutedWork().decrementBurstTime();
                    for (int i = 0; i < jobs.size(); i++)
                        if (jobs.get(i).getBurstTime() != 0 && jobs.get(i).getArrivalTime() <= CPUtime && jobs.get(i) != w.getExecutedWork())
                            jobs.get(i).incrementWaitingTime();
                }
                if (w.getExecutedWork().getBurstTime() == 0) w.setExitTime(CPUtime);
            }
        }
        grid();
    }


    private static int maxBurstTime() {
        //Returns the biggest burst time from the arraylist of jobs.
        int biggest = jobs.get(0).getBurstTime();
        for (int i = 0; i < jobs.size(); i++)
            if (jobs.get(i).getBurstTime() > biggest) biggest = jobs.get(i).getBurstTime();

        return biggest;

    }


    private static int getIdHighestPriority() {
        //returns the index for the job with the highest priority (if the bust time isn't zero). -1 if there's no jobs awaiting execution.
        int highest = 0;
        for (int i = 1; i < jobs.size(); i++)
            if ((jobs.get(i).getBurstTime() != 0) && (jobs.get(highest).getBurstTime() == 0 || jobs.get(i).getPriority() < jobs.get(highest).getPriority()))
                highest = i;

        if (jobs.get(highest).getBurstTime() > 0) return highest;
        return -1;
    }

    public static void priority(String pathname) {
        //schedules jobs to the CPU according to their priority, with the assumption that 1 is the highest.
        jobs = new ArrayList<>();
        scanPriority(pathname);
        algorithmName = "Priority scheduling";
        if (jobs.size() == 0) {
            System.err.println("No jobs in the system for " + algorithmName + ".");
            return;
        }
        works = new ArrayList<>();
        CPUtime = 0;
        while (maxBurstTime() != 0) {
            Work w = new Work();
            works.add(w);
            w.setExecutedWork(jobs.get(getIdHighestPriority()));
            w.setBurstTime(w.getExecutedWork().getBurstTime());
            w.setEntryTime(CPUtime);
            while (w.getExecutedWork().getBurstTime() != 0) {
                w.getExecutedWork().decrementBurstTime();
                for (int i = 0; i < jobs.size(); i++)
                    if (jobs.get(i).getBurstTime() != 0 && jobs.get(i).getArrivalTime() <= CPUtime && jobs.get(i) != w.getExecutedWork())
                        jobs.get(i).incrementWaitingTime();
                CPUtime++;
            }
            w.setExitTime(CPUtime);
        }
        grid();

    }


    private static void sort() {
        //Sorting algorithm to sort the jobs.
        quickSort(jobs, jobs.size() - 1, 0);
    }

    private static void quickSort(ArrayList<Job> a, int high, int low) {
        if (low >= high) return;

        int pivot = a.get(high).getArrivalTime();
        int p1 = low;
        int p2 = high;
        while (p1 < p2) {
            while (a.get(p1).getArrivalTime() <= pivot && p1 < p2) p1++;
            while (a.get(p2).getArrivalTime() >= pivot && p2 > p1) p2--;
            swap(a, p1, p2);
        }
        swap(a, p1, (high));
        quickSort(a, p1 - 1, low);
        quickSort(a, high, p1 + 1);

    }

    private static void swap(ArrayList<Job> a, int p1, int p2) {
        Job temp = a.get(p1);
        a.set(p1, a.get(p2));
        a.set(p2, temp);
    }

    private static int maxPivotLength() {
        //Returns the maximum possible length for the pivot.
        int biggestBurst = works.get(0).getBurstTime();
        for (int i = 1; i < works.size(); i++)
            if (works.get(i).getBurstTime() > biggestBurst) biggestBurst = works.get(i).getBurstTime();

        int longestNameL = jobs.get(0).getName().length();
        for (int i = 1; i < jobs.size(); i++)
            if (jobs.get(i).getName().length() > longestNameL) longestNameL = works.get(i).getBurstTime();

        return 1 + biggestBurst + (longestNameL * 2) + ((String.valueOf(biggestBurst).length() * 2) + 1) * 2;
    }

    private static void grid() {
        //creating the layout of the gantt chart as well as printing it to the screen. To visualize the execution timeline of the jobs.
        System.out.println("\n\n" + algorithmName + " algorithm:");
        Character[][] grid = new Character[maxPivotLength() * works.size() + 1][6]; //+1 because we start from zero.
        int positionX = 0;
        for (int j = 0; j < works.size(); j++)
            if (works.get(j).getBurstTime() != 0) {
                int pivot = works.get(j).getBurstTime();  //pivot is the name of the length of each box that stores the works information in the chart.
                //The pivot is dependent on the size of the job's burst and the job's name.
                if (works.get(j).getExitTime() == 0)
                    pivot = (works.get(j + 1).getEntryTime() - works.get(j).getEntryTime());
                if (pivot <= 0) {
                    System.err.println("Pivot is less than or equal to zero.");
                    return;
                }
                if (pivot % 2 != 0) pivot++; //Pivot has to be an even number to display the work attributes properly.
                String n = works.get(j).getExecutedWork().getName();
                if (n.length() >= (pivot / 2)) pivot = pivot + n.length() * 2;
                //storing the starting burst time and stopping burst time for each work in the middle of the box, just bellow the job's name.
                String startingBurst = String.valueOf(works.get(j).getBurstTime());
                if (works.get(j).getExitTime() != 0) {
                    int l = startingBurst.length() + 2;
                    if (l >= (pivot / 2)) {
                        pivot = pivot + l * 2;
                    }
                    int i = 0;
                    int position = ((positionX + pivot / 2) - (l / 2));
                    for (; i < startingBurst.length(); i++)
                        grid[position + i][3] = startingBurst.charAt(i);
                    grid[position + (i++)][3] = ',';
                    grid[position + i][3] = '0';

                } else {
                    String stoppingBurst = String.valueOf(works.get(j).getBurstTime() - (works.get(j + 1).getEntryTime() - works.get(j).getEntryTime()));
                    int l = startingBurst.length() + stoppingBurst.length() + 1;
                    if (l >= (pivot / 2)) {
                        pivot = pivot + l * 2;
                    }
                    int i = 0;
                    int position = ((positionX + pivot / 2) - (l / 2));
                    for (; i < startingBurst.length(); i++)
                        grid[position + i][3] = startingBurst.charAt(i);
                    grid[position + (i++)][3] = ',';
                    for (int p = 0; p < stoppingBurst.length(); p++)
                        grid[position + p + i][3] = stoppingBurst.charAt(p);
                }
                for (int i = positionX + 1; i < positionX + pivot; i++)
                    grid[i][0] = '-';
                positionX += pivot;

                for (int k = 0; k < 5; k++)
                    grid[positionX][k] = '|';

                //storing the name for each job in the middle of the box that represent work.
                for (int i = 0; i < n.length(); i++)
                    grid[((positionX - pivot / 2) - n.length() / 2) + i][2] = n.charAt(i);


                for (int l = positionX - 1; l > positionX - pivot; l--)
                    grid[l][4] = '-';
                for (int m = 4; m >= 0; m--)
                    grid[positionX - pivot][m] = '|';
                String eTime = String.valueOf(works.get(j).getEntryTime());
                for (int i = 0; eTime.length() <= 5 && i < eTime.length(); i++)
                    grid[positionX - pivot + i][5] = eTime.charAt(i);
            }
        String eTime = String.valueOf(works.get(works.size() - 1).getExitTime());
        for (int i = 0; eTime.length() <= 5 && i < eTime.length(); i++)
            grid[positionX - (eTime.length() - 1) + i][5] = eTime.charAt(i);

        for (int i = 0; i < grid.length; i++)
            if (grid[i][0] != null) System.out.print(grid[i][0]);
            else System.out.print(" ");

        System.out.println();
        for (int i = 0; i < grid.length; i++)
            if (grid[i][1] != null) System.out.print(grid[i][1]);
            else System.out.print(" ");

        System.out.println();
        for (int i = 0; i < grid.length; i++)
            if (grid[i][2] != null) System.out.print(grid[i][2]);
            else System.out.print(" ");

        System.out.println();
        for (int i = 0; i < grid.length; i++)
            if (grid[i][3] != null) System.out.print(grid[i][3]);
            else System.out.print(" ");

        System.out.println();
        for (int i = 0; i < grid.length; i++)
            if (grid[i][4] != null) System.out.print(grid[i][4]);
            else System.out.print(" ");

        System.out.println();
        for (int i = 0; i < grid.length; i++)
            if (grid[i][5] != null) System.out.print(grid[i][5]);
            else System.out.print(" ");

        System.out.println();

        StringBuilder s = new StringBuilder("Average waiting time = (");
        double sum = 0;
        int a;
        for (int i = 0; i < works.size(); i++)
            if (works.get(i).getExitTime() != 0) {
                a = works.get(i).getExecutedWork().getWaitingTime();
                sum += a;
                if (i != works.size() - 1)
                    s.append(works.get(i).getExecutedWork().getName()).append("=").append(a).append(" + ");
                else
                    s.append(works.get(i).getExecutedWork().getName()).append("=").append(a).append(")=").append((int) sum).append("/").append(jobs.size()).append(" = ").append(Math.round((sum / jobs.size()) * 1000d) / 1000d);
            }
        System.out.println(s);
        s = new StringBuilder("Average completion time = (");
        sum = 0;
        a = 0;
        for (int i = 0; i < works.size(); i++)
            if (works.get(i).getExitTime() != 0) {
                a = (works.get(i).getExitTime());
                sum += a;
                if (i != works.size() - 1)
                    s.append(works.get(i).getExecutedWork().getName()).append("=").append(a).append(" + ");
                else
                    s.append(works.get(i).getExecutedWork().getName()).append("=").append(a).append(")=").append((int) sum).append("/").append(jobs.size()).append(" = ").append(Math.round((sum / jobs.size()) * 1000d) / 1000d);
            }

        System.out.print(s + "\n");

    }


}//end of class CPUScheduler.

class Job {
    private String name;
    private int burstTime;
    private int arrivalTime;
    private int waitingTime;
    private int priority;

    public Job() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void incrementWaitingTime() {
        waitingTime++;
    }

    public void decrementBurstTime() {
        if (burstTime != 0)
            burstTime--;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getPriority() {
        return priority;
    }
}//end of class Job.

class Work {
    private Job executedWork;
    private int burstTime;
    private int entryTime;
    private int exitTime;

    public Job getExecutedWork() {
        return executedWork;
    }


    public void setExecutedWork(Job executedWork) {
        this.executedWork = executedWork;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public int getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(int entryTime) {
        this.entryTime = entryTime;
    }

    public int getExitTime() {
        return exitTime;
    }

    public void setExitTime(int exitTime) {
        this.exitTime = exitTime;
    }

}//end of class Work.

