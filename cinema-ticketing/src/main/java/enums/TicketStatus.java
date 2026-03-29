package enums;

public enum TicketStatus {
    PENDING("Chờ thanh toán"),
    PAID("Đã thanh toán"),
    CANCELLED("Đã hủy"),
    EXPIRED("Hết hạn");

    private final String displayName;

    TicketStatus (String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
