package com.dti.cornell.events;

/**
 * Created by jaggerbrulato on 3/20/18.
 */

/**
 * Alternative to functional programming in Java 7. A wrapper for a function that some type T.
 */
public interface Callback<T>
{
    void execute(T t);
}