import os
import matplotlib.pyplot as plt
import numpy as np
from matplotlib.colors import ListedColormap

def save_simulation_frames(json_array, static_root="./static"):
    """
    Saves simulation frames from a JSON array to a series of images in a new folder.
    """

    # create the static root directory if it doesn't exist
    os.makedirs(static_root, exist_ok=True)

    # find the next available index for the new simulation folder
    existing_dirs = [
        d for d in os.listdir(static_root)
        if d.startswith("simulation_") and d[len("simulation_"):].isdigit()
    ]
    existing_indices = [int(d.split("_")[1]) for d in existing_dirs]
    new_index = max(existing_indices, default=-1) + 1

    new_folder = os.path.join(static_root, f"simulation_{new_index}")
    os.makedirs(new_folder)

    colormap = ListedColormap([
        "#000000",  # 0 - empty
        "#228B22",  # 1 - Oak (forêt classique)
        "#006400",  # 2 - Pine (vert foncé)
        "#7CFC00",  # 3 - Grass (vert clair)
        "#1E90FF",  # 4 - Water (bleu)
        "#A9A9A9",  # 5 - Rock (gris)
        "#FFA500",  # 6 - Burning (orange)
        "#2F4F4F",  # 7 - Burned (gris foncé)
    ])

    # get grid size from the first frame
    all_cells = json_array[0]["cells"]
    max_x = max(cell["x"] for cell in all_cells)
    max_y = max(cell["y"] for cell in all_cells)
    grid_size_x = max_x + 1
    grid_size_y = max_y + 1

    # create a grid for each frame and save as image
    for i, frame in enumerate(json_array):
        grid = np.zeros((grid_size_y, grid_size_x), dtype=int)

        for cell in frame["cells"]:
            x, y = cell["x"], cell["y"]
            state = cell["state"]
            ctype = cell["type"]

            if state == "Burning":
                grid[y][x] = 6  # orange
            elif state == "Burned":
                grid[y][x] = 7  # gris foncé
            elif state == "Unburned":
                # color based on type
                grid[y][x] = {
                    "Oak": 1,
                    "Pine": 2,
                    "Grass": 3,
                    "Water": 4,
                    "Rock": 5
                }.get(ctype, 0)
            else:
                grid[y][x] = 0

        fig, ax = plt.subplots(figsize=(5, 5))
        ax.imshow(grid, cmap=colormap, vmin=0, vmax=7)
        ax.axis('off')

        # save the figure
        img_path = os.path.join(new_folder, f"frame_{i:03}.png")
        plt.savefig(img_path, bbox_inches='tight', pad_inches=0)
        plt.close(fig)

    print(f"{len(json_array)} images enregistrées dans {new_folder}")
    return new_folder

