# General purpose Segment Tree

A parametric, fully persistent Segment Tree which supports fast updates (through lazy propagation).

ComputeFunction \<T\>, ModifyFunction \<T\> and RangeFunction \<T\> can be freely implemented 
to obtain the required structure.

- ComputeFunction \<T\> explains how to compute the value of node N, starting from its children's values.
  For example, for a MinSegmentTree the required _compute_ would be something like:

  ComputeFunction \<T\> compute = (T v1, T v2) -> {
    if(v1 == null)
      return v2;
    if(v2 == null)
      return v1;
    return v1.compareTo(v2) <= 0 ? v1 : v2;
  }
                               
- ModifyFunction \<T\> explains how to replace a leaf during an update.
  For example, for a Segment Tree which supports an operation such as update(pos, increment),
  the required _modify_ would be something like:
  
  ModifyFunction \<T\> modify = (T v, T increment) -> {
    return v == null ? increment : v.add(increment);
  }
  
- RangeFunction \<T\> is necessary for lazy propagation. It explains how to change the value of an
  internal node, postponing the update of its subtree.
  As for a structure allowing update(l, r, increment) and query(l, r) = sum(a[l], ..., a[r]), 
  _range_ would state something like: N.value.add(increment.multiply(r - l + 1)).

T has to be a type supporting the operations needed by the desired Segment Tree, of course.
