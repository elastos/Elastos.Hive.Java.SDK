package org.elastos.hive.provider;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

public class RunStatsController {
    private RunStatsAPI runStatsAPI;

    public RunStatsController(NodeRPCConnection connection) {
        this.runStatsAPI = connection.createService(RunStatsAPI.class, true);
    }

    public List<VaultDetail> getVaults() throws HiveException {
        try {
            return runStatsAPI.getVaults().execute().body().getVaults();
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

    public List<BackupDetail> getBackups() throws HiveException {
        try {
            return runStatsAPI.getBackups().execute().body().getBackups();
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

    public List<FilledOrderDetail> getFilledOrders() throws HiveException {
        try {
            return runStatsAPI.getFilledOrders().execute().body().getPayments();
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
}
