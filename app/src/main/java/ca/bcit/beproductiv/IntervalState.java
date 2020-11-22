package ca.bcit.beproductiv;

public enum IntervalState {
    INTERVAL_ONE(0, false),
    BREAK_ONE(1, true),
    INTERVAL_TWO(1, false),
    BREAK_TWO(2, true),
    INTERVAL_THREE(2, false),
    COMPLETED_ALL(3, false){
        @Override
        public IntervalState next() {
            return values()[0];
        }
    };
    private final int  checkMarks;
    private final boolean isBreak;
    public IntervalState next() {
        // No bounds checking required here, because the last instance overrides
        return values()[ordinal() + 1];
    }
    IntervalState(int numCheckMarks, boolean isBreak){
        checkMarks = numCheckMarks;
        this.isBreak = isBreak;
    }
    public int getCheckMarks(){
        return checkMarks;
    }

    public boolean isBreak() {
        return isBreak;
    }
}
