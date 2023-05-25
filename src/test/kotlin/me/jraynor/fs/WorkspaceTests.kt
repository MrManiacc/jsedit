package me.jraynor.fs


import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.jraynor.fs.FileStore
import me.jraynor.fs.workspace.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class WorkspaceTest {

    private lateinit var workspace: Workspace
    private lateinit var fileStore: FileStore
    private lateinit var inputStream: ByteArrayInputStream
    private lateinit var outputStream: ByteArrayOutputStream
    private lateinit var action: Action
    private lateinit var agent: Agent
    private lateinit var subscription: Subscription

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        fileStore = mockk()
        action = mockk()
        agent = mockk()
        subscription = mockk()

        val actionJson = action.toString().toByteArray(Charsets.UTF_8)
        inputStream = ByteArrayInputStream(actionJson)
        outputStream = ByteArrayOutputStream()

        workspace = Workspace(fileStore, inputStream, outputStream)
        workspace.subscribe("testMethod", subscription)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test subscribe`() {
        // Prepare
        val newSubscription = mockk<Subscription>()

        // Act
        workspace.subscribe("testMethod", newSubscription)

        // Assert
        verify { agent.subscribe(newSubscription) }
    }

    @Test
    fun `test post`() {
        val agent = mockk<Agent>(relaxed = true)
        val fs = mockk<FileStore>()
        val workspace = Workspace(fs, inputStream, outputStream)

        every { agent.respond(action) } returns Unit

        workspace.post(action)

        verify { agent.respond(action) }
    }

    @Test
    fun `test start`() = runBlocking {
        // Prepare
        val actionJson = action.toString()
        coEvery { action.string("method") } returns "testMethod"

        // Act
        workspace.start()

        // Assert
        coVerify { agent.respond(action) }
    }

    @Test
    fun `test sendAction`() = runBlocking {
        // Prepare
        val actionJson = action.toString()

        // Act
        workspace.sendAction(action)

        // Assert
        val outputStreamAsString = String(outputStream.toByteArray(), Charsets.UTF_8)
        assert(outputStreamAsString.contains("Content-Length: ${actionJson.length}\r\n\r\n$actionJson"))
    }

    @Test
    fun testJsonReadFromString() {
        val subJsonObject = "{\"poop\": \"pee\"}"
        val json = "{\"name\": \"test\",\"age\": 23,\"active\": true, \"blah\": $subJsonObject}"
        val workspaceAction = Action.fromJson(json)
        assertEquals("test", workspaceAction.parameters["name"])
        assertEquals(23, workspaceAction.parameters["age"])
        assertEquals(true, workspaceAction.parameters["active"])
    }

    @Test
    fun testInitializeAction() {
        val action = Actions.initialize()
        assertEquals("initialize", action.parameters["method"])

    }

}
