#!/bin/bash

# 启动MCP Server的脚本
# 激活虚拟环境并启动markitdown-mcp服务

echo "激活虚拟环境..."
source myenv/bin/activate

echo "启动MCP Server..."
markitdown-mcp --http --host 127.0.0.1 --port 3001