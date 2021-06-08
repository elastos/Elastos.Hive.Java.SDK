package org.elastos.hive.exception;

public class PricingPlanNotFoundException extends EntityNotFoundException {
	private static final long serialVersionUID = -5537222473332097613L;

	public PricingPlanNotFoundException() {
        super();
    }

    public PricingPlanNotFoundException(String message) {
        super(message);
    }

    public PricingPlanNotFoundException(RPCException e) {
        super(e.getMessage());
    }
}
