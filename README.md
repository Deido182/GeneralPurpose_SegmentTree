# GeneralPurpose_SegmentTree

A prefabricated Segment Tree which supports fast updates (implements lazy propagation).

"General purpose" because anyone can launch a different object of Function1 <T>, Function2 <T> and Function3 <T> 
to obtain the required structure.

- Function1 <T> f1 explains how to compute the value for node N, starting from the values of its children.
  For example, for a MinSegmentTree the required f1 would be something like:

  Function <T> f1 = (T v1, T v2) -> {
    if(v1 == null)
      return v2;
    if(v2 == null)
      return v1;
    return v1.compareTo(v2) <= 0 ? v1 : v2;
  }
