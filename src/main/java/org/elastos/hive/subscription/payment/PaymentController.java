package org.elastos.hive.subscription.payment;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

/**
 * The payment controller is the wrapper class for accessing the payment module.
 */
public class PaymentController {
	private PaymentAPI paymentAPI;

	public PaymentController(NodeRPCConnection connection) {
		paymentAPI = connection.createService(PaymentAPI.class, true);
	}

	/**
	 * This is for creating the payment order which upgrades the pricing plan of the vault or the backup.
	 *
	 * @param subscription The value is "vault" or "backup".
	 * @param pricingName The pricing plan name.
	 * @return The details of the order.
	 * @throws HiveException The error comes from the hive node.
	 */
	public Order placeOrder(String subscription, String pricingName) throws HiveException {
		try {
			return paymentAPI.placeOrder(new CreateOrderParams(subscription, pricingName)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Pay the order by the order id and the transaction id.
	 *
	 * @param orderId The order id of payment contract
	 * @return The order which is the proof of the payment of the order for the user.
	 * @throws HiveException The error comes from the hive node.
	 */
	public Receipt settleOrder(int orderId) throws HiveException {
		try {
			return paymentAPI.settleOrder(Integer.toString(orderId)).execute().body();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the order information of the vault by the order id.
	 *
	 * @param orderId  of payment contract
	 * @return The details of the order.
	 * @throws HiveException The error comes from the hive node.
	 */
	public Order getVaultOrder(int orderId) throws HiveException {
		List<Order> orders = getOrdersInternal("vault", Integer.toString(orderId));
		return orders.get(0);
	}

	/**
	 * Get the order information of the backup by the order id.
	 *
	 * @param orderId  of payment contract
	 * @return The details of the order.
	 * @throws HiveException The error comes from the hive node.
	 */
	public Order getBackupOrder(int orderId) throws HiveException {
		List<Order> orders = getOrdersInternal("backup", Integer.toString(orderId));
		return orders.get(0);
	}

	/**
	 * Get the orders by the subscription type.
	 *
	 * @param subscription The value is "vault" or "backup".
	 * @return The order list, MUST not empty.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<Order> getOrders(String subscription) throws HiveException {
		return getOrdersInternal(subscription, null);
	}

	private List<Order> getOrdersInternal(String subscription, String orderId) throws HiveException {
		try {
			return paymentAPI.getOrders(subscription, orderId).execute().body().getOrders();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the receipt by the order id.
	 *
	 * @param orderId The order id.
	 * @return The details of the receipt.
	 * @throws HiveException The error comes from the hive node.
	 */
	public Receipt getReceipt(int orderId) throws HiveException {
		List<Receipt> receipts = this.getReceiptsInternal(Integer.toString(orderId));
		return receipts.get(0);
	}

	/**
	 * Get the receipts belongs to the current user.
	 *
	 * @return The details of the receipt.
	 * @throws HiveException The error comes from the hive node.
	 */
	public List<Receipt> getReceipts() throws HiveException {
		return this.getReceiptsInternal(null);
	}

	private List<Receipt> getReceiptsInternal(String orderId) throws HiveException {
		try {
			return paymentAPI.getReceipts(orderId).execute().body().getReceipts();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				case NodeRPCException.BAD_REQUEST:
					throw new InvalidParameterException(e.getMessage());
				case NodeRPCException.NOT_FOUND:
					throw new NotFoundException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}

	/**
	 * Get the version of the payment module.
	 *
	 * @return The version.
	 * @throws HiveException The error comes from the hive node.
	 */
	public String getVersion() throws HiveException {
		try {
			return paymentAPI.getVersion().execute().body().getVersion();
		} catch (NodeRPCException e) {
			switch (e.getCode()) {
				case NodeRPCException.UNAUTHORIZED:
					throw new UnauthorizedException(e);
				case NodeRPCException.FORBIDDEN:
					throw new VaultForbiddenException(e);
				default:
					throw new ServerUnknownException(e);
			}
		} catch (IOException e) {
			throw new NetworkException(e);
		}
	}
}
