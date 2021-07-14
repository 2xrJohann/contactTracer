import java.util.*;

public class ContactTracer {

    HashMap<Integer, HashMap<String, HashSet>> contactTimes;
    HashMap<String, HashSet<Integer>> personTimes;
    HashSet<Integer> times;

    /**
     * Initialises an empty ContactTracer with no populated contact traces.
     */
    public ContactTracer() {
        contactTimes = new HashMap();
        personTimes = new HashMap<String, HashSet<Integer>>();
        times = new HashSet<Integer>();
    }

    /**
     * Initialises the ContactTracer and populates the internal data structures
     * with the given list of contract traces.
     * 
     * @param traces to populate with
     * @require traces != null
     */
    public ContactTracer(List<Trace> traces) {
        contactTimes = new HashMap();
        personTimes = new HashMap<String, HashSet<Integer>>();
        times = new HashSet<Integer>();

        for(int i = 0; i < traces.size(); i++){
            addTrace(traces.get(i));
        }
    }

    /**
     * Adds a new contact trace to 
     * 
     * If a contact trace involving the same two people at the exact same time is
     * already stored, do nothing.
     * 
     * @param trace to add
     * @require trace != null
     */
    public void addTrace(Trace trace) {
        String person1 = trace.getPerson1();
        String person2 = trace.getPerson2();
        int time = trace.getTime();
        addToTime(person1, person2, time);

    }

    private void addToTime(String person1, String person2, Integer time){
        HashMap temp = new HashMap();

        if(contactTimes.containsKey(time)){
            temp = contactTimes.get(time);
            temp.put(person1, person2);
            temp.put(person2, person1);
            contactTimes.put(time, temp);
        }
        else{
            temp.put(person1, person2);
            temp.put(person2, person1);
            contactTimes.put(time, temp);
        }

        times.add(time);

        if(personTimes.containsKey(person1)){
            HashSet<Integer> timeSet = personTimes.get(person1);
            timeSet.add(time);
            personTimes.put(person1, timeSet);
        }else{
            HashSet<Integer> timeSet = new HashSet();
            timeSet.add(time);
            personTimes.put(person1, timeSet);
        }

        if(personTimes.containsKey(person2)){
            HashSet<Integer> timeSet = personTimes.get(person2);
            timeSet.add(time);
            personTimes.put(person2, timeSet);
        }else{
            HashSet<Integer> timeSet = new HashSet();
            timeSet.add(time);
            personTimes.put(person2, timeSet);
        }

    }

    /**
     * Gets a list of times that person1 and person2 have come into direct 
     * contact (as per the tracing data).
     *
     * If the two people haven't come into contact before, an empty list is returned.
     * 
     * Otherwise the list should be sorted in ascending order.
     * 
     * @param person1 
     * @param person2
     * @return a list of contact times, in ascending order.
     * @require person1 != null && person2 != null
     */
    public List<Integer> getContactTimes(String person1, String person2) {
        List returnList = new ArrayList();

        HashSet personTimes1 = personTimes.get(person1);
        HashSet personTimes2 = personTimes.get(person2);

        for (Object i : personTimes1){
            if(personTimes2.contains(i)){
                returnList.add(i);
            }
        }

        Collections.sort(returnList);

        return returnList;
    }

    /**
     * Gets all the people that the given person has been in direct contact with
     * over the entire history of the tracing dataset.
     * 
     * @param person to list direct contacts of
     * @return set of the person's direct contacts
     */
    public Set<String> getContacts(String person) {
        HashSet returnSet = new HashSet();
        HashSet timesTemp = personTimes.get(person);
        for (Object i : timesTemp){
            returnSet.add(contactTimes.get(i).get(person));
        }
        return returnSet;
    }

    /**
     * Gets all the people that the given person has been in direct contact with
     * at OR after the given timestamp (i.e. inclusive).
     * 
     * @param person to list direct contacts of
     * @param timestamp to filter contacts being at or after
     * @return set of the person's direct contacts at or after the timestamp
     */
    public Set<String> getContactsAfter(String person, int timestamp) {

        HashSet returnSet = new HashSet();
        HashSet timesTemp = personTimes.get(person);

        for (Object i : timesTemp){
            if((Integer)i >= (Integer)timestamp) {
                returnSet.add(contactTimes.get(i).get(person));
            }
        }
        return returnSet;
    }

    /**
     * Initiates a contact trace starting with the given person, who
     * became contagious at timeOfContagion.
     * 
     * Note that the return set shouldn't include the original person the trace started from.
     * 
     * @param person to start contact tracing from
     * @param timeOfContagion the exact time person became contagious
     * @return set of people who may have contracted the disease, originating from person
     */
    public Set<String> contactTrace(String person, int timeOfContagion) {

        ArrayList peopleWhoHaveIt = new ArrayList();
        peopleWhoHaveIt.add(person);

        Object[] timeArray =  times.toArray();
        Arrays.sort(timeArray);

        HashMap contagionTime = new HashMap();
        contagionTime.put(person, (Integer)timeOfContagion);

        for(Object i : timeArray){
            if((Integer)i < (Integer)timeOfContagion) {
                continue;
            }else{
                for(int j = 0; j < peopleWhoHaveIt.size(); j++){
                    if(contactTimes.get(i).containsKey(peopleWhoHaveIt.get(j))){
                        if((Integer) i > (Integer)contagionTime.get(peopleWhoHaveIt.get(j)) + 60 ){
                            String currentPerson = (String)peopleWhoHaveIt.get(j);
                            if(!peopleWhoHaveIt.contains((contactTimes.get(i).get(currentPerson)))) {
                                peopleWhoHaveIt.add((contactTimes.get(i).get(currentPerson)));
                                contagionTime.put((contactTimes.get(i).get(currentPerson)), i);
                            }
                        }
                    }
                }
            }
        }
        peopleWhoHaveIt.remove(0);
        return new HashSet(peopleWhoHaveIt);
    }
}