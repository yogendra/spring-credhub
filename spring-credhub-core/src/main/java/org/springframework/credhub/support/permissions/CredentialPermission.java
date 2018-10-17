/*
 *
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.springframework.credhub.support.permissions;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.credhub.support.CredentialRequest;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Permissions applied to a credential in CredHub. If provided when a
 * credential is written, these values will control what actors can access update
 * or retrieve the credential.
 *
 * Objects of this type are constructed by the application and passed
 * as part of a {@link CredentialRequest}.
 *
 * @author Scott Frederick
 */
public class CredentialPermission {
	private final Actor actor;

	@JsonProperty
	private final List<Operation> operations;

	/**
	 * Create a {@literal CredentialPermission}.
	 */
	@SuppressWarnings("unused")
	private CredentialPermission() {
		this.actor = null;
		this.operations = null;
	}

	/**
	 * Create a set of permissions. Intended to be used internally.
	 * Clients should use {@link #builder()} to construct instances of this class.
	 *
	 * @param actor the ID of the entity that will be allowed to access the credential
	 * @param operations the operations that the actor will be allowed to perform on the
	 * credential
	 */
	private CredentialPermission(Actor actor, List<Operation> operations) {
		this.actor = actor;
		this.operations = operations;
	}

	/**
	 * Get the ID of the entity that will be allowed to access the credential.
	 *
	 * @return the ID
	 */
	public Actor getActor() {
		return this.actor;
	}

	/**
	 * Get the set of operations that the actor will be allowed to perform on
	 * the credential.
	 *
	 * @return the operations
	 */
	public List<Operation> getOperations() {
		return operations;
	}

	/**
	 * Get the set of operations that the actor will be allowed to perform on
	 * the credential.
	 *
	 * @return the operations
	 */
	@JsonGetter("operations")
	private List<String> getOperationsAsString() {
		if (operations == null) {
			return null;
		}
		
		List<String> operationValues = new ArrayList<String>(operations.size());
		for (Operation operation : operations) {
			operationValues.add(operation.operation());
		}
		return operationValues;
	}

	/**
	 * Create a builder that provides a fluent API for providing the values required
	 * to construct a {@link CredentialPermission}.
	 *
	 * @return a builder
	 */
	public static CredentialPermissionBuilder builder() {
		return new CredentialPermissionBuilder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof CredentialPermission))
			return false;

		CredentialPermission that = (CredentialPermission) o;

		if (actor != null ? !actor.equals(that.actor) : that.actor != null)
			return false;
		return operations != null ? operations.equals(that.operations)
				: that.operations == null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(actor, operations);
	}

	@Override
	public String toString() {
		return "CredentialPermission{"
				+ "actor='" + actor + '\''
				+ ", operations=" + operations
				+ '}';
	}

	/**
	 * A builder that provides a fluent API for constructing {@link CredentialPermission}
	 * instances.
	 */
	public static class CredentialPermissionBuilder {
		private Actor actor;
		private ArrayList<Operation> operations;

		CredentialPermissionBuilder() {
		}

		/**
		 * Set the ID of an application that will be assigned permissions on a credential.
		 * This will often be a Cloud Foundry application GUID.
		 *
		 * @param appId application ID; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialPermissionBuilder app(String appId) {
			Assert.notNull(appId, "appId must not be null");
			Assert.isNull(actor, "only one actor can be specified");
			this.actor = Actor.app(appId);
			return this;
		}

		/**
		 * Set the ID of a user that will be assigned permissions on a credential.
		 * This is typically a GUID generated by UAA when a user account is created.
		 *
		 * @param userId user ID; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialPermissionBuilder user(String userId) {
			Assert.notNull(userId, "userId must not be null");
			Assert.isNull(actor, "only one actor can be specified");
			this.actor = Actor.user(userId);
			return this;
		}

		/**
		 * Set the ID of a user that will be assigned permissions on a credential.
		 * This is typically a GUID generated by UAA when a user account is created.
		 *
		 * @param zoneId zone ID; must not be {@literal null}
		 * @param userId user ID; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialPermissionBuilder user(String zoneId, String userId) {
			Assert.notNull(zoneId, "zoneId must not be null");
			Assert.notNull(userId, "userId must not be null");
			Assert.isNull(actor, "only one actor can be specified");
			this.actor = Actor.user(zoneId, userId);
			return this;
		}

		/**
		 * Set the ID of an OAuth2 client that will be assigned permissions on a credential.
		 *
		 * @param clientId OAuth2 client ID; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialPermissionBuilder client(String clientId) {
			Assert.notNull(clientId, "clientId must not be null");
			Assert.isNull(actor, "only one actor can be specified");
			this.actor = Actor.client(clientId);
			return this;
		}

		/**
		 * Set the ID of an OAuth2 client that will be assigned permissions on a credential.
		 *
		 * @param zoneId zone ID; must not be {@literal null}
		 * @param clientId OAuth2 client ID; must not be {@literal null}
		 * @return the builder
		 */
		public CredentialPermissionBuilder client(String zoneId, String clientId) {
			Assert.notNull(zoneId, "zoneId must not be null");
			Assert.notNull(clientId, "clientId must not be null");
			Assert.isNull(actor, "only one actor can be specified");
			this.actor = Actor.client(zoneId, clientId);
			return this;
		}

		/**
		 * Set an {@link Operation} that the actor will be allowed to perform on
		 * the credential. Multiple operations can be provided with consecutive calls to
		 * this method.
		 *
		 * @param operation the {@link Operation}
		 * @return the builder
		 */
		public CredentialPermissionBuilder operation(Operation operation) {
			initOperations();
			this.operations.add(operation);
			return this;
		}

		/**
		 * Specify a set of {@link Operation}s that the actor will be allowed to perform
		 * on the credential.
		 *
		 * @param operations the {@link Operation}s
		 * @return the builder
		 */
		public CredentialPermissionBuilder operations(Operation... operations) {
			initOperations();
			this.operations.addAll(Arrays.asList(operations));
			return this;
		}

		private void initOperations() {
			if (this.operations == null) this.operations = new ArrayList<Operation>();
		}

		/**
		 * Construct a {@link CredentialPermission} with the provided values.
		 *
		 * @return a {@link CredentialPermission}
		 */
		public CredentialPermission build() {
			List<Operation> operations;
			switch (this.operations == null ? 0 : this.operations.size()) {
				case 0:
					operations = java.util.Collections.emptyList();
					break;
				case 1:
					operations = java.util.Collections.singletonList(this.operations.get(0));
					break;
				default:
					operations = java.util.Collections.unmodifiableList(new ArrayList<Operation>(this.operations));
			}

			return new CredentialPermission(actor, operations);
		}
	}
}
