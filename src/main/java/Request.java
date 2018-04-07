/**
 * Created by pramraj on 4/5/18.
 */
public class Request {
    private Integer fromFloor;
    private Integer toFloor;

    public Request(Integer fromFloor, Integer toFloor) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    public Integer getFromFloor() {
        return fromFloor;
    }

    public Integer getToFloor() {
        return toFloor;
    }
}
