# TODO List for Project Refinement

This file describes the main tasks that need to be done to enhance the project. This involves reworking the existing systems and adding new features.

## Rework Operating System to Workspace

The current system that simulates a computer's full file system needs to be restructured. The goal is to create a system that represents a "project" with a defined file structure, similar to how an Integrated Development Environment (IDE) organizes projects.

## Rework the Virtual Machine System

The current virtual machine system requires rework. The Workspace should manage all scripting environment contexts, enhancing efficiency and organization.

## Implement JSON-RPC Communication System

Complete the implementation of the JSON-RPC communication system between the LSP provided by GraalVM and our editor. This is a key communication link that will allow the software to respond accurately to the user's actions.

## Add Run Configuration Selection and Run Button on the Main Menu Bar

A run configuration selection and a run button should be added to the main menu bar. Other debug buttons can also be included, creating a comprehensive debug project bar for easy access to these features.

## Rework the File Explorer

The current positioning of the file explorer needs to be changed. It should be moved to the bottom left, next to the console, allowing the text editor to occupy the full upper half of the screen. This will offer better visibility and usability.

## Implement LSP Client on top of the JSON-RPC Client

Once the JSON-RPC client is complete, implement the Language Server Protocol (LSP) client on top of it. This includes features like go to definition, find usages, and auto completion. This will improve the text editor's functionality and user experience.

