# GeneralPurpose_SegmentTree

A prefabricated Segment Tree which supports fast updates (implements lazy propagation).

"General purpose" because anyone can launch a different object of Function1 \<T\>, Function2 \<T\> and Function3 \<T\> 
to obtain the required structure.

- Function1 \<T\> f1 explains how to compute the value for node N, starting from the values of its children.
  For example, for a MinSegmentTree the required f1 would be something like:

  Function1 \<T\> f1 = (T v1, T v2) -> {
    if(v1 == null)
      return v2;
    if(v2 == null)
      return v1;
    return v1.compareTo(v2) <= 0 ? v1 : v2;
  }
                               
- Function2 \<T\> f2 explains how to replace a leaf during an update.
  For example, if you need a Segment Tree which support an operation such that update(pos, increment),
  the required f2 would be something like:
  
  Function2 \<T\> f2 = (T v, T increment) -> {
    return v == null ? increment : v.add(increment);
  }
  
- Function3 \<T\> f3 is necessary for the lazy propagation. It explains how to change the value of an
  internal node (which cover some range). Let's suppose to want a structure which allows
  update(l, r, increment) and query(l, r) = sum(a[l], ..., a[r]).
  Now let's suppose to reach an internal node N which covers exactly the whole range [l, r].
  We need to change its value without going on. Indeed, we will go on with the update of its subtree
  only after a call of query(l', r') with l <= l' <= r' <= r.
  In such cases f3 would say something like: N.value.add(increment.multiply(r - l + 1)).

T has to be a type which supports the operations needed by the desired Segment Tree of course.
