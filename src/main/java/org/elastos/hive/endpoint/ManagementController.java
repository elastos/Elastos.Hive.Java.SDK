package org.elastos.hive.endpoint;

import org.elastos.hive.connection.NodeRPCConnection;
import org.elastos.hive.connection.NodeRPCException;
import org.elastos.hive.exception.*;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

public class ManagementController {
    private ManagementAPI managementAPI;

    public ManagementController(NodeRPCConnection connection) {
        this.managementAPI = connection.createService(ManagementAPI.class, true);
    }

    public List<VaultDetail> getVaults() throws HiveException {
        try {
            return managementAPI.getVaults().execute().body().getVaults();
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
            return managementAPI.getBackups().execute().body().getBackups();
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

    public List<UserDetail> getUsers() throws HiveException {
        try {
            return managementAPI.getUsers().execute().body().getUsers();
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

    public List<PaymentDetail> getPayments() throws HiveException {
        try {
            return managementAPI.getPayments().execute().body().getPayments();
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

    public void deleteVaults(List<String> userDids) throws HiveException {
        try {
            if (userDids == null) {
                throw new InvalidParameterException("Invalid parameter userDids");
            }
            managementAPI.deleteVaults(new DeleteVaultsParams(userDids)).execute().body();
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

    public void deleteBackups(List<String> userDids) throws HiveException {
        try {
            if (userDids == null) {
                throw new InvalidParameterException("Invalid parameter userDids");
            }
            managementAPI.deleteBackups(new DeleteBackupsParams(userDids)).execute().body();
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

    public List<VaultAppDetail> getVaultApps() throws HiveException {
        try {
            return managementAPI.getVaultApps().execute().body().getApps();
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

    public void deleteVaultApps(List<String> appDids) throws HiveException {
        try {
            if (appDids == null) {
                throw new InvalidParameterException("Invalid parameter appDids");
            }
            managementAPI.deleteVaultApps(new DeleteVaultAppsParams(appDids)).execute().body();
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
