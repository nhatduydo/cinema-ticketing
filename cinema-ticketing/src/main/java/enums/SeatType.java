package enums;

public enum SeatType {
    STANDARD("Ghế thường"),
    VIP("Ghế VIP"),
    COUPLE("Ghế đôi");

    private final String displayName;
    SeatType (String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
