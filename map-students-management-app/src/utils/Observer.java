package utils;

public interface Observer<E extends Event>  {
    /**
     * Update function for an observer object
     */
    void update(E e);
}
