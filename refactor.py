import os
import shutil

base_dir = r"c:\Users\bryan\Desktop\UNMSM\CICLO 2026 - 1\Estructura de Datos\Pruebas de Proyecto\Proyecto\src\main\java"
old_base = os.path.join(base_dir, "com", "distribuidora", "inventario")

mapping = {
    "exceptions": "excepciones",
    "models": "modelos",
    "services": "servicios",
    "structures": "estructuras",
    "ui": "ui"
}

# 1. Update file contents
for root, dirs, files in os.walk(old_base):
    for f in files:
        if f.endswith(".java"):
            filepath = os.path.join(root, f)
            with open(filepath, "r", encoding="utf-8") as file:
                content = file.read()
            
            # replace imports and packages
            for k, v in mapping.items():
                content = content.replace(f"com.distribuidora.inventario.{k}", v)
            
            # for Main.java, remove the package declaration if it's there
            content = content.replace("package com.distribuidora.inventario;\n", "")
            content = content.replace("package com.distribuidora.inventario;", "")
            
            with open(filepath, "w", encoding="utf-8") as file:
                file.write(content)

# 2. Move files and folders
for k, v in mapping.items():
    src = os.path.join(old_base, k)
    dst = os.path.join(base_dir, v)
    if os.path.exists(src):
        shutil.move(src, dst)

# Move Main.java
main_src = os.path.join(old_base, "Main.java")
main_dst = os.path.join(base_dir, "Main.java")
if os.path.exists(main_src):
    shutil.move(main_src, main_dst)

# Delete empty directories
com_dir = os.path.join(base_dir, "com")
if os.path.exists(com_dir):
    shutil.rmtree(com_dir)

print("Refactoring completado con éxito.")
