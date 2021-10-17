package nl.rabobank.account;

import nl.rabobank.authorizations.PowerOfAttorney;

import java.util.Set;

public interface Account
{
    String getAccountNumber();
    String getAccountHolderName();
    Double getBalance();
    Set<PowerOfAttorney> getGrants();
}
