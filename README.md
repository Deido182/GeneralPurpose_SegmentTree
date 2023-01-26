# GeneralPurpose_SegmentTree

A prefabricated Segment Tree which supports fast updates (implements lazy propagation).

"General purpose" because anyone can launch a different object of ComputeFunction \<T\>, ModifyFunction \<T\> and RangeFunction \<T\> 
to obtain the required structure.

- ComputeFunction \<T\> compute explains how to compute the value for node N, starting from the values of its children.
  For example, for a MinSegmentTree the required compute would be something like:

  ComputeFunction \<T\> compute = (T v1, T v2) -> {
    if(v1 == null)
      return v2;
    if(v2 == null)
      return v1;
    return v1.compareTo(v2) <= 0 ? v1 : v2;
  }
                               
- ModifyFunction \<T\> modify explains how to replace a leaf during an update.
  For example, if you need a Segment Tree which support an operation such as update(pos, increment),
  the required modify would be something like:
  
  ModifyFunction \<T\> modify = (T v, T increment) -> {
    return v == null ? increment : v.add(increment);
  }
  
- RangeFunction \<T\> range is necessary for the lazy propagation. It explains how to change the value of an
  internal node (which covers a range). Let's suppose to want a structure which allows
  update(l, r, increment) and query(l, r) = sum(a[l], ..., a[r]).
  Now let's suppose to reach an internal node N which covers exactly the whole range [l, r].
  We need to change its value without going on. Indeed, we will go on with the update of its subtree
  only after a call of query(l', r') with l <= l' <= r' <= r.
  In such cases range would say something like: N.value.add(increment.multiply(r - l + 1)).

T has to be a type which supports the operations needed by the desired Segment Tree of course.
