from PIL import Image
import os

script_dir = os.path.dirname(os.path.abspath(__file__))
project_root = os.path.dirname(script_dir)

source_img = os.path.join(script_dir, "screen.png")
res_dir = os.path.join(project_root, "app", "src", "main", "res")

sizes = {
    "mdpi": 48,
    "hdpi": 72,
    "xhdpi": 96,
    "xxhdpi": 144,
    "xxxhdpi": 192
}

try:
    with Image.open(source_img) as img:
        for density, size in sizes.items():
            dir_path = os.path.join(res_dir, f"mipmap-{density}")
            os.makedirs(dir_path, exist_ok=True)
            
            resized_img = img.resize((size, size), Image.Resampling.LANCZOS)
            
            # Save as ic_launcher.webp and ic_launcher_round.webp to match common Android configs
            resized_img.save(os.path.join(dir_path, "ic_launcher.png"), "PNG")
            resized_img.save(os.path.join(dir_path, "ic_launcher_round.png"), "PNG")
            
    print("Icons generated successfully!")
except Exception as e:
    print(f"Error: {e}")
