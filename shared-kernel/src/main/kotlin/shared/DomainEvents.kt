package shared

import java.util.Locale
import java.util.UUID

sealed interface DomainEvent

sealed interface TicketEvent : DomainEvent

data class TicketCreated(
    val id: UUID,
    val title: String,
    val description: String,
    val customerId: UUID,
    val type: String
) : TicketEvent

sealed interface TicketPriorityEvent : DomainEvent

data class TicketPrioritized(val id: UUID, val ticketId: UUID, val priority: String) : TicketPriorityEvent

sealed interface AssignmentEvent : DomainEvent

data class TicketAssigned(val ticketId: UUID, val agentId: UUID) : AssignmentEvent

data class AssignmentAttemptFailed(val ticketId: UUID, val reason: Reason) : AssignmentEvent {
    enum class Reason { NO_AGENTS_AVAILABLE, NO_SUITABLE_AGENT_AVAILABLE }
}

data class TicketQueued(val ticketId: UUID) : AssignmentEvent

sealed interface AgentEvent : DomainEvent

data class AgentCreated(val id: UUID, val email: String, val fullName: String, val skills: List<String>, val languages: List<Locale>) : AgentEvent

data class AgentConnected(val id: UUID) : AgentEvent

data class AgentDisconnected(val id: UUID) : AgentEvent

sealed interface CustomerEvent : DomainEvent

data class CustomerCreated(val id: UUID, val fullName: String, val email: String, val preferredLang: String, val tier: String) : CustomerEvent

//customer-mgmt/agent-mgmt
//
//Different Domain Responsibilities
//Agent Management:
//Focuses on managing agents as users or resources:
//Lifecycle: Create, update, activate, deactivate.
//Roles and permissions: Tier 1, Tier 2, Supervisor.
//Availability: Online/offline status.
//Workload tracking.
//Assignment:
//Focuses on managing ticket-to-agent relationships:
//Assignment rules (e.g., assigning tickets based on workload, skills, or SLA priorities).
//Reassignments and escalations.
//Tracking which agent is responsible for which tickets.
//
//agent-workspace bc would provide list of
//
//1. Potential Bounded Context: Agent Workspace
//A dedicated bounded context called the Agent Workspace Context could be created to handle the agent's user experience, including their "home" and tools for managing tasks and tickets.
//
//2. Responsibilities of the Agent Workspace Context
//a. Aggregating Data Across Contexts
//The Agent Workspace Context would aggregate data from multiple other bounded contexts (e.g., Ticket Management, Prioritization, Assignment) to provide the agent with a unified view of their tasks, tickets, and workflows.
//
//It doesn’t "own" ticket data or prioritization logic but consumes them through queries or event streams.
//It focuses on presenting information to the agent and orchestrating workflows relevant to them.
//b. Agent-Specific Workflows
//This context could manage workflows that are specific to agents, such as:
//
//Listing assigned tickets or tasks.
//Displaying priorities, deadlines, or SLAs.
//Highlighting notifications (e.g., escalations, unresolved tickets).
//Managing workload (e.g., tracking active vs. completed tasks).
//c. Personalization and Dashboard
//Agents may have personalized dashboards or tools based on:
//
//Their role (e.g., Tier 1 vs. Tier 2 support).
//Their current workload.
//Recommendations or guidance (e.g., suggested tickets to handle next based on priority).