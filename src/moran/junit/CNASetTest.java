
package moran.junit;

import jam.math.EventSet;
import jam.math.Probability;

import moran.cna.CNASet;
import moran.cna.CNAType;

import org.junit.*;
import static org.junit.Assert.*;

public class CNASetTest {
    @Test public void testCreate() {
        Probability gainProb = Probability.valueOf(0.1);
        Probability lossProb = Probability.valueOf(0.2);
        Probability noneProb = Probability.valueOf(0.7);

        EventSet<CNAType> eventSet =
            CNASet.create(gainProb, lossProb);

        assertEquals(3, eventSet.getEventCount());

        assertTrue(eventSet.getEvents().contains(CNAType.GAIN));
        assertTrue(eventSet.getEvents().contains(CNAType.LOSS));
        assertTrue(eventSet.getEvents().contains(CNAType.NONE));

        assertEquals(gainProb, eventSet.getEventProbability(CNAType.GAIN));
        assertEquals(lossProb, eventSet.getEventProbability(CNAType.LOSS));
        assertEquals(noneProb, eventSet.getEventProbability(CNAType.NONE));
    }

    public static void main(String[] args) {
        org.junit.runner.JUnitCore.main("moran.junit.CNASetTest");
    }
}
