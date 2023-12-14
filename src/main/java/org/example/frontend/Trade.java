package org.example.frontend;

class Trade {
    private int tradeID;
    private User initiator;
    private User receiver;
    private Card offeredCard;
    private Card requestedCard;
    private TradeStatus status;

    // Constructor
    public Trade(int tradeID, User initiator, User receiver, Card offeredCard, Card requestedCard) {
        this.tradeID = tradeID;
        this.initiator = initiator;
        this.receiver = receiver;
        this.offeredCard = offeredCard;
        this.requestedCard = requestedCard;
        this.status = TradeStatus.PENDING;
    }

    // Getter and Setter methods (omitted for brevity)

    public void accept() {
        if (status == TradeStatus.PENDING) {
            // Transfer offered card from initiator to receiver
            initiator.removeCard(offeredCard);
            receiver.addCard(offeredCard);

            // Transfer requested card from receiver to initiator
            receiver.removeCard(requestedCard);
            initiator.addCard(requestedCard);

            this.status = TradeStatus.ACCEPTED;
            System.out.println("Trade accepted successfully!");
        } else {
            System.out.println("Cannot accept a trade that is not pending.");
        }
    }

    public void reject() {
        if (status == TradeStatus.PENDING) {
            this.status = TradeStatus.REJECTED;
            System.out.println("Trade rejected.");
        } else {
            System.out.println("Cannot reject a trade that is not pending.");
        }
    }
}
