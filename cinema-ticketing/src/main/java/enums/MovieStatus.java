package enums;

public enum MovieStatus {
    NOW_SHOWING("Đang chiếu"),
    COMING_SOON("Sắp chiếu"),
    ENDED("Đã kết thúc");

//    mỗi status có 1 tên hiển thị
//    final = không đổi sau khi tạo, private = chỉ truy cập trong enum này
    private final String displayName;
    MovieStatus(String displayName){
        this.displayName = displayName;
    }

    public String getDisplayName(){
        return displayName;
    }
}
