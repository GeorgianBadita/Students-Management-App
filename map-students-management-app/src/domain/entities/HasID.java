package domain.entities;


/**
 * Interface for ID
 * @param <ID> Requires ID
 */
public interface HasID<ID> {
    ID getId();
    void setId(ID id);
}