package com.rtbhouse.kafka.workers.impl.range;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.stream.LongStream;

import com.google.common.collect.ImmutableList;

public class RangeUtils {

    public static List<ClosedRange> rangesFromLongs(Iterable<Long> longs) {
        ImmutableList.Builder<ClosedRange> listBuilder = ImmutableList.builder();
        BasicClosedRange.Builder rangeBuilder = null;

        for (Long offset : longs) {
            if (rangeBuilder == null) {
                rangeBuilder = BasicClosedRange.builder(offset);
                continue;
            }

            checkState(offset > rangeBuilder.getLastOffset());

            if (offset == rangeBuilder.getLastOffset() + 1) {
                rangeBuilder.extend(offset);
            } else {
                listBuilder.add(rangeBuilder.build());
                rangeBuilder = BasicClosedRange.builder(offset);
            }
        }

        if (rangeBuilder != null) {
            listBuilder.add(rangeBuilder.build());
        }

        return listBuilder.build();
    }

    public static LongStream elementsStream(ClosedRange closedRange) {
        return LongStream.rangeClosed(closedRange.lowerEndpoint(), closedRange.upperEndpoint());
    }

    public static LongStream reverseElementsStream(ClosedRange closedRange) {
        long lowerEndpoint = closedRange.lowerEndpoint();
        long upperEndpoint = closedRange.upperEndpoint();
        return LongStream.rangeClosed(lowerEndpoint, upperEndpoint).map(i -> upperEndpoint - i + lowerEndpoint);
    }
}
