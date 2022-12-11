package pct.skedulo;

import pct.skedulo.model.Show;

import java.util.*;
import java.util.stream.Collectors;

public class ShowScheduler {

    public LinkedList<Show> optimalShows(List<Show> unSortedShows) {
        LinkedList<Show> optimalOrderShows = optimalOrderShows(unSortedShows);
        return buildTimeOptimalScheduleShows(optimalOrderShows);
    }

    public LinkedList<Show> optimalOrderShows(List<Show> unSortedShows) {
        LinkedList<Show> optimalOrderShows = new LinkedList<>();
        List<Show> showsSortedByStartTime = sortedShowsByStartTime(unSortedShows);
        Show firstShow = findTheEarliestShow(showsSortedByStartTime);
        optimalOrderShows.add(firstShow);
        showsSortedByStartTime.remove(firstShow);

        populateTheRestShows(firstShow, showsSortedByStartTime, optimalOrderShows);

        return optimalOrderShows;
    }


    public void populateTheRestShows(Show previousShow, List<Show> sortedShows, LinkedList<Show> result) {
        Show nextShow = findTheNextShow(previousShow, sortedShows);

        if (nextShow == null) {
            Show extendedShow = result.stream()
                    .filter(s -> s.getPriority() < result.getLast().getPriority() && s.getFinish().after(result.getLast().getFinish()))
                    .max(Comparator.comparing(Show::getPriority))
                    .orElse(null);
            if (extendedShow != null) {
                result.add(extendedShow);
                populateTheRestShows(extendedShow, sortedShows, result);
            }
        } else {
            result.add(nextShow);
            sortedShows.remove(nextShow);

            // find in the current shows, if any lower priority show has the finish time after the next
            Show extendedShow = result.stream()
                    .filter(s -> s.getPriority() < nextShow.getPriority() && s.getFinish().after(nextShow.getFinish()))
                    .max(Comparator.comparing(Show::getPriority))
                    .orElse(null);

            if (extendedShow != null) {
                if (sortedShows.stream().anyMatch(s -> s.getPriority() > extendedShow.getPriority() && s.getStart().before(nextShow.getFinish()))) {
                    populateTheRestShows(nextShow, sortedShows, result);
                } else {
                    // if there is no higher priority show than the extended show
                    result.add(extendedShow);
                }
            }
            populateTheRestShows(nextShow, sortedShows, result);
        }
    }

    public Show findTheEarliestShow(List<Show> sortedShows) {
        Show firstShow = sortedShows.get(0);
        List<Show> showsHasSameStartTime = sortedShows.stream()
                .filter(s -> s.getStart().compareTo(firstShow.getStart()) == 0)
                .collect(Collectors.toList());

        if (showsHasSameStartTime.isEmpty()) {
            return firstShow;
        } else {
            return showsHasSameStartTime.stream().max(Comparator.comparingInt(Show::getPriority)).get();
        }
    }

    public Show findTheNextShow(Show previousShow, List<Show> sortedShows) {
        if (sortedShows.isEmpty()) {
            return null;
        }

        Show nextCandidate = findTheEarliestShow(sortedShows);

        if (nextCandidate == null) {
            return null;
        }

        if (nextCandidate.getPriority() < previousShow.getPriority() && !nextCandidate.getFinish().after(previousShow.getFinish())) {
            sortedShows.remove(nextCandidate);
            return findTheNextShow(previousShow, sortedShows);
        }

        if (nextCandidate.getPriority() < previousShow.getPriority()) {
            // if nextCandidate's priority lower than the previous show's priority,
            // could have some other shows that have priority higher than the previous and start before the previous show's finish.
            Show betterCandidate = sortedShows.stream()
                    .filter(s -> s.getPriority() > previousShow.getPriority() && s.getStart().before(previousShow.getFinish()))
                    .sorted(Comparator.comparing(s -> s.getStart())).findFirst().orElse(null);
            if (betterCandidate != null) {
                if (!nextCandidate.getFinish().after(betterCandidate.getFinish())) {
                    sortedShows.remove(nextCandidate);
                }
                nextCandidate = betterCandidate;
            }
        }

        // Handling duplicated time + with or w/o priority
        Date startDate = nextCandidate.getStart();
        String band = nextCandidate.getBand();
        int newPriority = nextCandidate.getPriority();

        // same start time and priority
        List<Show> dupShows = sortedShows.stream()
                .filter(s -> s.getPriority() == newPriority && s.getStart().compareTo(startDate) == 0)
                .collect(Collectors.toList());
        if (dupShows.size() > 1) {
            Comparator<Show> comparator = Comparator.comparing(Show::getFinish);
            nextCandidate = dupShows.stream().max(comparator).get();
            dupShows.remove(nextCandidate);
            sortedShows.removeAll(dupShows);
        }

        // same start time, different priority
        dupShows = sortedShows.stream()
                .filter(s -> !s.getBand().equals(band) && s.getStart().compareTo(startDate) == 0 && s.getPriority() > newPriority)
                .sorted(Comparator.comparingInt(Show::getPriority))
                .collect(Collectors.toList());
        if (!dupShows.isEmpty()) {
            nextCandidate = dupShows.get(0);
        }



        if (nextCandidate != null && previousShow.getFinish().before(nextCandidate.getStart())) {
            // there is a gap between 2 shows
            // find any show that can step in
            Show finalNextShow = nextCandidate;
            Show stepIn = sortedShows.stream()
                    .filter(s -> s.getPriority() < finalNextShow.getPriority() && s.getStart().before(finalNextShow.getStart()))
                    .max(Comparator.comparingInt(Show::getPriority)).orElse(null);
            if (stepIn != null) {
                return stepIn;
            }
        }

        // clean up the not able to watch shows
        // clean all lower priority than nextShow and finish before next Show
        Show finalNextShow = nextCandidate;
        List<Show> unWatchingShows = sortedShows.stream()
                .filter(s -> s.getPriority() < finalNextShow.getPriority() && s.getFinish().before(finalNextShow.getFinish()))
                .collect(Collectors.toList());
        sortedShows.removeAll(unWatchingShows);

        return nextCandidate;
    }

    public LinkedList<Show> buildTimeOptimalScheduleShows(LinkedList<Show> optimalShows) {
        LinkedList<Show> optimalScheduleShows = new LinkedList<>();
        int listSize = optimalShows.size();
        Show preShow;
        Show currentShow;
        Show nextShow;
        Show optimalShow;

        Date startTime;
        Date endTime;

        for (int i = 0 ; i < listSize; i++) {
            currentShow = optimalShows.get(i);
            if (i == 0) {
                optimalShow = new Show(currentShow.getBand(), currentShow.getStart(), optimalShows.get(i + 1).getStart());
            } else if (i == listSize - 1) {
                preShow = optimalShows.get(i -1);
                startTime = currentShow.getStart().after(preShow.getFinish()) ? currentShow.getStart() : preShow.getFinish();
                optimalShow = new Show(currentShow.getBand(), startTime, currentShow.getFinish());
            } else {
                preShow = optimalShows.get(i -1);
                nextShow = optimalShows.get(i + 1);
                if (currentShow.getPriority() > preShow.getPriority()) {
                    startTime = currentShow.getStart();
                } else {
                    startTime = currentShow.getStart().after(preShow.getFinish()) ? currentShow.getStart() : preShow.getFinish();
                }

                if (currentShow.getPriority() > nextShow.getPriority()) {
                    endTime = currentShow.getFinish();
                } else {
                    endTime = nextShow.getStart().after(currentShow.getFinish()) ? currentShow.getFinish() : nextShow.getStart();
                }

                optimalShow = new Show(currentShow.getBand(), startTime, endTime);
            }
            optimalScheduleShows.add(optimalShow);
        }
        return optimalScheduleShows;
    }

    public List<Show> sortedShowsByStartTime(List<Show> unSortedShows) {
        return unSortedShows.stream()
                .sorted(Comparator.comparing(Show::getStart))
                .collect(Collectors.toList());
    }
}
