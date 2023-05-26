package com.kuze.bigdata.study.funnel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App implements Serializable {

    public static List<Integer> findSpecStepEventIndex(List<Event> data, Integer step) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getStep() == step) {
                result.add(i);
            }
        }
        return result;
    }

    public static EventsTimestamp[] findFunnel(List<Event> data, Integer maxStep, Integer window) {
        for (Integer lastStep = maxStep; lastStep > 0; lastStep--) {
            List<Integer> lastStepSourceIndexArray = findSpecStepEventIndex(data, lastStep);
            if (lastStepSourceIndexArray.size() == 0) {
                continue;
            }

            for (int i = 0; i < lastStepSourceIndexArray.size(); i++) {
                int lastStepSourceIndex = lastStepSourceIndexArray.get(i);
                EventsTimestamp[] eventsTimestamp = new EventsTimestamp[lastStep + 1];
                Event lastEvent = data.get(lastStepSourceIndex);
                eventsTimestamp[lastStep] = new EventsTimestamp(lastEvent.getTime(), lastStepSourceIndex + 1);

                if (lastStep == 1) {
                    return Arrays.copyOfRange(eventsTimestamp, 1, eventsTimestamp.length);
                }

                int previousStep = lastStep - 1;
                List<Event> sliceData = data.subList(0, lastStepSourceIndex);
                for (int sourceIndex = sliceData.size() - 1; sourceIndex >= 0; sourceIndex--) {
                    Event event = sliceData.get(sourceIndex);
                    boolean timeMatch = window >= eventsTimestamp[eventsTimestamp.length - 1].getTime() - event.getTime();
                    if (event.getStep() == previousStep && timeMatch) {
                        eventsTimestamp[previousStep] =  new EventsTimestamp(event.getTime(), sourceIndex + 1);
                        previousStep--;
                        if (previousStep < 1) {
                            return Arrays.copyOfRange(eventsTimestamp, 1, eventsTimestamp.length);
                        }
                    }
                }
            }
        }
        return null;
    }
}
