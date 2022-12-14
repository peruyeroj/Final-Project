package pkgLogic;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.formula.functions.FinanceLib;

public class Loan {
	private double LoanAmount;
	private double LoanBalanceEnd;
	private double InterestRate;
	private int LoanPaymentCnt;
	private boolean bCompoundingOption;
	private LocalDate StartDate;
	private double AdditionalPayment;
	private int paymentAmt;
	private double Escrow;
	
	private HashMap<Integer, Double> hmRates = new HashMap<Integer, Double>();

	private ArrayList<Payment> loanPayments = new ArrayList<Payment>();

	public Loan(double loanAmount, double interestRate, int loanPaymentCnt, LocalDate startDate,
			double additionalPayment, double escrow, int AdjustLockTime, int AdjustLoan, double AdjustLoanRate) {
		super();
		
		loanPaymentCnt *= 12;
		LoanAmount = loanAmount;
		LoanPaymentCnt = loanPaymentCnt;
		StartDate = startDate;
		AdditionalPayment = additionalPayment;
		bCompoundingOption = false;
		LoanBalanceEnd = 0;
		this.Escrow = escrow;
		
		double tempInterestRate = interestRate;
		int tempAdjustLockTime = AdjustLockTime;
		
		for (int i = 1; i <= loanPaymentCnt; i++) {
			if((AdjustLoan != 0) && (AdjustLoanRate != 0)) {
				if(i > tempAdjustLockTime * 12) {
					tempInterestRate += AdjustLockTime;
					tempAdjustLockTime += AdjustLoan;
				}
			}
			hmRates.put(i, tempInterestRate);
		}
		

		double RemainingBalance = LoanAmount;
		
		int PaymentCnt = 1;
		
		while (RemainingBalance >= this.GetPMT() + this.AdditionalPayment) {
			   InterestRate = this.getInterestRate(PaymentCnt);
	           Payment payment = new Payment(RemainingBalance, PaymentCnt++, startDate, this, false);
	           RemainingBalance = payment.getEndingBalance();
	           startDate = startDate.plusMonths(1);
	           loanPayments.add(payment);
	       }
		Payment payment = new Payment(RemainingBalance, PaymentCnt++, startDate,this,true);
		loanPayments.add(payment);
	}

	public double GetPMT() {
		double PMT = 0;
		PMT = FinanceLib.pmt(this.getInterestRate(LoanPaymentCnt) / 12, this.getLoanPaymentCnt(), this.getLoanAmount(), this.getLoanBalanceEnd(),this.isbCompoundingOption());
		return Math.abs(PMT);
	}

	public double getTotalPayments() {
		double tot = 0;
		for (Payment p: this.loanPayments) {
			tot += p.getPayment();
		}
		return tot; 
	}

	public double getTotalInterest() {

		double interest = 0;
		for( Payment p: this.loanPayments) {
			interest += p.getPayment();
		}
		return interest - this.LoanAmount;
	}

	public double getTotalEscrow() {
		double escrow = 0;
		for(Payment p: this.loanPayments) {
			escrow += p.getEscrowPayment();
		}
		return escrow;

	}

	public double getLoanAmount() {
		return LoanAmount;
	}

	public void setLoanAmount(double loanAmount) {
		LoanAmount = loanAmount;
	}

	public double getLoanBalanceEnd() {
		return LoanBalanceEnd;
	}

	public void setLoanBalanceEnd(double loanBalanceEnd) {
		LoanBalanceEnd = loanBalanceEnd;
	}

	public double getInterestRate(int PaymentCnt) {
		return hmRates.get(PaymentCnt);
	}

	public void setInterestRate(double interestRate) {
		InterestRate = interestRate;
	}

	public int getLoanPaymentCnt() {
		return LoanPaymentCnt;
	}

	public void setLoanPaymentCnt(int loanPaymentCnt) {
		LoanPaymentCnt = loanPaymentCnt;
	}

	public boolean isbCompoundingOption() {
		return bCompoundingOption;
	}

	public void setbCompoundingOption(boolean bCompoundingOption) {
		this.bCompoundingOption = bCompoundingOption;
	}

	public LocalDate getStartDate() {
		return StartDate;
	}

	public void setStartDate(LocalDate startDate) {
		StartDate = startDate;
	}

	public double getAdditionalPayment() {
		return AdditionalPayment;
	}

	public void setAdditionalPayment(double additionalPayment) {
		AdditionalPayment = additionalPayment;
	}

	public ArrayList<Payment> getLoanPayments() {
		return loanPayments;
	}

	public void setLoanPayments(ArrayList<Payment> loanPayments) {
		this.loanPayments = loanPayments;
	}

	public double getEscrow() {
		return Escrow;
	}

}